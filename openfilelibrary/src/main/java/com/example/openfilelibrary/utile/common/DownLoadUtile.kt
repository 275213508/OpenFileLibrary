package com.example.openfilelibrary.utile.common

import android.os.Build
import android.os.Environment
import android.util.Patterns
import android.webkit.URLUtil
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.example.openfilelibrary.base.ICell
import com.example.openfilelibrary.http.FileDownloadService
import com.hjq.toast.Toaster
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * @author zyju
 * @date 2024/9/10 14:44
 */
object DownLoadUtile {

    fun downloadFile(context: FragmentActivity, dirpath: String, downUri: String, fileName: String?, icell:ICell<File>) {
        var filepath = dirpath + "/" + getFileName1(downUri)
        if (!fileName.isNullOrBlank()) {
            filepath = "$dirpath/$fileName"
        }
        val isValid = URLUtil.isValidUrl(downUri) && Patterns.WEB_URL.matcher(downUri).matches()
        if (!isValid) {
            icell.cell(File(downUri))
            return
        }
        if (FileUtils.isFileExists(filepath)) {
            XPopup.Builder(context).asConfirm(
                "提示", "该文件已存在，是否重新下载？",
                {
                    icell.cell(File(filepath))
                }, {
                    downloadFiles(context, dirpath, downUri, fileName, object : ICell<File> {
                        override fun cell(cell: File) {
                            icell.cell(cell)
                        }
                    })
                }
            )
                .setConfirmText("打开")
                .setCancelText("重新下载并打开")
                .show()
        }else {
            downloadFiles(context, dirpath, downUri, fileName, object : ICell<File> {
                override fun cell(cell: File) {
                    icell.cell(cell)
                }
            })
        }
    }
    private fun download(
        base: String,
        downUri: String,
        dirpath: String,
        fileName: String?,
        dialog: LoadingPopupView,
        icell: ICell<File>
    ) {
        ThreadUtils.getIoPool().submit {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(base)
                .build()
            val fileDownloadService = retrofit.create(FileDownloadService::class.java)
            val call: Call<ResponseBody>? = fileDownloadService.downloadFile(downUri.replace(base, ""))
            var file = getSaveFilePath(dirpath, downUri, fileName)
            LogUtils.i("文件:${base}${downUri.replace(base, "")}")
            call?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    // 处理响应体
                    if (response.isSuccessful()) {
                        // 获取响应体
                        val body: ResponseBody? = response.body()
                        // 使用输入流保存响应体到文件
                        ThreadUtils.getIoPool().submit {
                            try {
                                FileUtils.delete(file)
                                FileUtils.createOrExistsFile(file)
//                                FileIOUtils.writeFileFromIS(file, body?.byteStream()){
//                                    LogUtils.i("下载中：" + it)
//                                }
                                saveFile(body!!,file,object :ICell<Int>{
                                    override fun cell(cell: Int) {
                                        dialog.setTitle("正在下载中：$cell/100")
                                    }

                                })
                                ThreadUtils.runOnUiThread {
                                    LogUtils.d("下载完成：" + file.toUri().path)
                                    dialog.dismiss()
                                    icell.cell(file)
                                }
                            } catch (e: Exception) {
                                LogUtils.i("下载失败：" + e.message)
                                file.delete()
                            }
                        }
                    }
                }


                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    dialog.dismiss()
                    file.delete()
                    LogUtils.d("下载失败：" + t.message)
                    t.printStackTrace()
                }
            })
        }
    }

    private fun downloadFiles(context: FragmentActivity, dirpath: String, downUri: String, fileName: String?, icell:ICell<File>) {
        var dialog = XPopup.Builder(context)
            .asLoading("正在加载中")
        dialog.show()
        var urs = downUri.split(downUri.toUri().host.toString())
        if (urs.size < 2) {
            LogUtils.i("下载地址错误")
            return
        }
        var base = urs[0] +downUri.toUri().authority
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionUtils.requestWriteSettings(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    download(base, downUri, dirpath, fileName, dialog, icell)
                }

                override fun onDenied() {
                    dialog.dismiss()
                   Toaster.show("需要读写权限才能下载文件")
                }

            })
        }else {
            download(base, downUri, dirpath, fileName, dialog, icell)
        }
    }
    private fun saveFile(body: ResponseBody,file: File,iCell: ICell<Int>) {
        // 创建输入流
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            // 定义目标文件
//            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "downloaded_file.txt")
            outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            val fileSize = body.contentLength()
            var totalBytesRead: Long = 0

            inputStream = body.byteStream()
            var bytesRead: Int
            while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead.toLong()
                // 计算并显示进度
                val progress = ((totalBytesRead * 100) / fileSize).toInt()
                // 在 UI 线程中更新进度条
                runOnUiThread {iCell.cell(progress) }
            }

            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // 关闭流
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getSaveFilePath(dirpath: String, downUri: String, fileName: String?): File {
        var filepath = dirpath + "/" + getFileName1(downUri)
        if (!fileName.isNullOrBlank()) {
            filepath = "$dirpath/$fileName"
        }
        var file = File(filepath)
        return file
    }
}
package com.example.openfilelibrary

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.webkit.URLUtil
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.example.openfilelibrary.base.ICell
import com.example.openfilelibrary.image.PreImageDialog
import com.example.openfilelibrary.pdf.PDFPreView
import com.example.openfilelibrary.tbs.TBSPreView
import com.example.openfilelibrary.txt.TxtPreView
import com.example.openfilelibrary.utile.TbsInstance
import com.example.openfilelibrary.utile.common.DownLoadUtile
import com.example.openfilelibrary.utile.common.PermissionManager
import com.example.openfilelibrary.utile.common.config
import com.example.openfilelibrary.utile.common.getSuffixName1
import com.example.openfilelibrary.video.OpenVideoPlayActivity
import com.hjq.toast.Toaster
import com.lxj.xpopup.XPopup
import com.tencent.tbs.reader.TbsFileInterfaceImpl
import com.wx.android.common.util.SharedPreferencesUtils
import java.io.File


/**
 * @author zyju
 * @date 2024/9/5 16:59
 */
class OpenFileViewModel {


    /**
     * @param fileUrl 文件下载地址
     * */
    fun openPDF(context: FragmentActivity, downUri: String) {
        openPDF(context, context.filesDir.path, downUri, "")
    }

    fun openPDF(context: FragmentActivity, downUri: String, fileName: String?) {
        openPDF(context, context.filesDir.path, downUri, fileName, null)
    }

    fun openPDF(context: FragmentActivity, savePath: String, downUri: String, fileName: String?, listener: DialogInterface.OnDismissListener? = null) {
        DownLoadUtile.downloadFile(context, savePath, downUri, fileName, object : ICell<File> {
            override fun cell(cell: File) {
                PDFPreView(cell.toUri()).setOnDismissListener(listener).show(context.supportFragmentManager)
            }
        })
    }

    fun openTBS(context: FragmentActivity, fileUrl: String, fileName: String?, APP_File_Provider: String): Boolean {
        return openTBS(context, fileUrl, fileName, APP_File_Provider, null)
    }

    /**
     * 打开腾讯Tbs阅读器
     * @return true:打开腾讯阅读器 false:tbs没有使用权限,需要购买
     * */
    fun openTBS(context: FragmentActivity, fileUrl: String, fileName: String?, APP_File_Provider: String, listener: DialogInterface.OnDismissListener? = null): Boolean {
        try {
            var filePrivater = getfilePrivate(context, APP_File_Provider)
            var isSanHu = SPUtils.getInstance().getBoolean(config.isSanHuApp)
            val suffix = if (fileName.isNullOrBlank()) getSuffixName1(fileUrl) else fileName
            var isinit = SharedPreferencesUtils.getInt(TbsInstance.TBS)
            LogUtils.i("TbsPreViewCallback url:", "openTBS: $fileUrl/$fileName/$APP_File_Provider")
            if (isSanHu && isinit == 1 && TbsFileInterfaceImpl.canOpenFileExt(File(fileUrl).extension)) {
                LogUtils.i("TBS打开文档", "openTBS: $fileUrl/$fileName/$APP_File_Provider")
                TBSPreView(fileUrl.toUri(), filePrivater).setOnDismissListener(listener).show(context.supportFragmentManager)
                return true
            }
        }catch (e: Exception){
            return false
        }catch (e:Throwable){
            return false
        }
        LogUtils.i("TBS无法打开文档", "openTBS: $fileUrl/$fileName/$APP_File_Provider")
        return false
    }


    fun openTxt(context: FragmentActivity, txtUrl: String, savePath: String? = context.filesDir.path) {
        openTxt(context, txtUrl, savePath, null)

    }

    fun openTxt(context: FragmentActivity, txtUrl: String, savePath: String? = context.filesDir.path, listener: DialogInterface.OnDismissListener? = null) {
        DownLoadUtile.downloadFile(
            context,
            savePath ?: context.filesDir.path,
            txtUrl,
            null,
            object : ICell<File> {
                override fun cell(cell: File) {
                    FileIOUtils.readFile2String(cell).let { text ->
                        TxtPreView(cell.name, text, null).setOnDismissListener(listener).show(context.supportFragmentManager)
                    }
                }
            })
    }

    fun openHTML(context: FragmentActivity, txtUrl: String, listener: DialogInterface.OnDismissListener? = null) {
        DownLoadUtile.downloadFile(
            context,
            context.filesDir.path,
            txtUrl,
            null,
            object : ICell<File> {
                override fun cell(cell: File) {
                    FileIOUtils.readFile2String(cell).let { text ->
                        TxtPreView(cell.name, text, 1).setOnDismissListener(listener).show(context.supportFragmentManager)
                    }
                }
            })
    }

    fun openImage(context: FragmentActivity, downUri: String) {
        openImage(context, downUri, "", null)
    }

    fun openImage(context: FragmentActivity, downUri: String, fileName: String? = "") {
        openImage(context, downUri, fileName, null)
    }

    fun openImage(context: FragmentActivity, downUri: String, fileName: String? = "", listener: DialogInterface.OnDismissListener? = null) {
        openImage(context, mutableListOf(downUri), mutableListOf(fileName), listener)
    }

    fun openImage(context: FragmentActivity, downUri: List<String>? = listOf(), listener: DialogInterface.OnDismissListener? = null) {
        openImage(context,downUri,null, listener)
    }
    fun openImage(context: FragmentActivity, downUri: List<String>? = listOf(), fileNames: List<String?>? = listOf(), listener: DialogInterface.OnDismissListener? = null) {
        context.runOnUiThread {
            var dialog = PreImageDialog(context, downUri, fileNames, 1)
            dialog.setOnDismissListener { listener?.onDismiss(dialog) }
            dialog.show()
        }
    }

//    var folioReader: FolioReader? = null
//    fun openEpub(context: FragmentActivity, path: String) {
//        try {
//            folioReader = FolioReader.get()
//                .setOnClosedListener {
//                    if (folioReader != null) {
//                        FolioReader.clear()
//                        FolioReader.stop()
//                    }
//                }
//            var config = AppUtil.getSavedConfig(context)
//            if (config == null) {
//                config = Config()
//            }
//            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL)
//            folioReader?.setConfig(config, true)?.openBook(path)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }


    private var video: ActivityResultLauncher<Intent>? = null
    /**
     * 打开视频
     * @param videoUrl 视频地址
     * 视频不做存储，直接展示
     * */
    fun openVideo(context: FragmentActivity, videoUrl: String, fileName: String?) {
        openVideo(context,videoUrl,fileName,null)
    }
    /**
     * 打开视频
     * @param videoUrl 视频地址
     * 视频不做存储，直接展示
     * */
    fun openVideo(context: FragmentActivity, videoUrl: String, fileName: String?,listener: ActivityResultLauncher<Intent>? = null) {
        //跳转视频录制页面的Result
        var intent = Intent(context, OpenVideoPlayActivity::class.java).putExtra("videoUrl", videoUrl).putExtra("fileName", fileName)
        if (listener==null){
            context.startActivity(intent)
        }else {
            listener.launch(intent)
        }
        context.overridePendingTransition(0, 0)
//        var uri = Uri.parse(videoUrl);
//        var intent = Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "video/*");
//        context.startActivity(intent);
//        VideoPreView(videoUrl).show(context.supportFragmentManager, null)
    }
    /**
     * 打开音频
     * @param videoUrl 音频地址
     * */
    fun openAudio(context: FragmentActivity, videoUrl: String) {
        openAudio(context,videoUrl,null)
    }
    /**
     * 打开音频
     * @param videoUrl 音频地址
     * */
    fun openAudio(context: FragmentActivity, videoUrl: String,listener: ActivityResultLauncher<Intent>? = null) {
        var uri = Uri.parse(videoUrl);
        var intent = Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/*");
        if (listener==null){
            context.startActivity(intent);
        }else {
            listener.launch(intent)
        }
        context.overridePendingTransition(0, 0)
//        context.startActivity(intent);
//        VideoPreView(videoUrl.toUri()).show(context.supportFragmentManager, null)
    }
//    /**
//     * 打开DOC文档（基于BaseBottomSheetFrag和WebView的实现）
//     * @param context 上下文
//     * @param docUrl DOC文档地址
//     * @param savePath 保存路径
//     * @param fileName 文件名
//     * @param listener 关闭监听器
//     */
//    fun openDocWithWebView(context: FragmentActivity, docUrl: String, savePath: String? = context.filesDir.path, fileName: String? = null, listener: DialogInterface.OnDismissListener? = null) {
//        DownLoadUtile.downloadFile(
//            context,
//            savePath ?: context.filesDir.path,
//            docUrl,
//            fileName,
//            object : ICell<File> {
//                override fun cell(cell: File) {
//                    // 使用基于BaseBottomSheetFrag和WebView的DOC预览
//                    DocWebViewPreview.showDocPreview(
//                        activity = context,
//                        filePath = cell.absolutePath,
//                        listener = listener
//                    )
//                }
//            }
//        )
//    }
    /**
     * @param listener 当前版本不支持其他格式返回反馈
     * */
    fun openOther(context: FragmentActivity, downUri: String, filePrivate: String = "",listener: ActivityResultLauncher<Intent>? = null) {
        var filePrivater = getfilePrivate(context, filePrivate)
        val isValid = URLUtil.isValidUrl(downUri) && Patterns.WEB_URL.matcher(downUri).matches()
        if (!isValid) {
            try {
                val file = File(downUri)
                // 检查文件是否可访问
                if (!PermissionManager.isFileAccessible(context, file)) {
                    Toaster.show("打开错误,请检查文件是否存在")
                    return
                }
                
                val intent = Intent(Intent.ACTION_VIEW)
                val fileUri = FileProvider.getUriForFile(context, filePrivater, file)
                intent.setDataAndType(fileUri, OpenFileUtils.getFileIntentType(file.extension))
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (listener==null){
                    context.startActivity(intent)
                    LogUtils.e("ActivityResultLauncher == null,打开前请调用 setActivityResultLauncher")
                }else {
                    listener.launch(intent)
                    context.overridePendingTransition(0, 0)
                }
            } catch (e: Exception) {
                LogUtils.e("openOther", "error:${e.message}\n $filePrivate")
                Toaster.show("未找到可打开此文件的应用")
            }
            return
        }
        XPopup.Builder(context).asConfirm(
            "提示", "即将跳转浏览器\n下载查看文件"
        ) {
            val url = downUri
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        }
            .show()
    }
    fun openOther(context: FragmentActivity, downUri: String, filePrivate: String = "") {
        openOther(context, downUri, filePrivate, null)
    }
}

/**
 * 获取文件的FileProvider
 * */
private fun getfilePrivate(context: FragmentActivity, APP_File_Provider: String): String {
    var filePrivater = APP_File_Provider
    if (filePrivater.isBlank()) {
        filePrivater = SPUtils.getInstance().getString(config.mFilePrivateKey, "")
        if (filePrivater.isNullOrBlank()) {
            filePrivater = context.packageName + ".fileprovider"
        }
    }
    return filePrivater
}
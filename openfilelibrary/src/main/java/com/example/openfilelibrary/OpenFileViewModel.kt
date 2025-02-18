package com.example.openfilelibrary

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.webkit.URLUtil
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
import com.example.openfilelibrary.utile.common.config
import com.example.openfilelibrary.utile.common.getSuffixName1
import com.example.openfilelibrary.video.OpenVideoPlayActivity
import com.folioreader.Config
import com.folioreader.FolioReader
import com.folioreader.util.AppUtil
import com.hjq.toast.Toaster
import com.lxj.xpopup.XPopup
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

    fun openPDF(context: FragmentActivity, savePath: String, downUri: String, fileName: String?) {
        DownLoadUtile.downloadFile(context, savePath, downUri, fileName, object : ICell<File> {
            override fun cell(cell: File) {
                PDFPreView(cell.toUri()).show(context.supportFragmentManager)
            }
        })
    }

    /**
     * 打开腾讯Tbs阅读器
     * @return true:打开腾讯阅读器 false:tbs没有使用权限,需要购买
     * */
    fun openTBS(context: FragmentActivity, fileUrl: String,fileName:String?, APP_File_Provider: String): Boolean {
        var filePrivater = getfilePrivate(context, APP_File_Provider)
        var isSanHu = SPUtils.getInstance().getBoolean(config.isSanHuApp)
        val suffix = if (fileName.isNullOrBlank()) getSuffixName1(fileUrl) else fileName
        var isinit =  SharedPreferencesUtils.getInt(TbsInstance.TBS)
        Log.i("TbsPreViewCallback :","isSanHu:$isSanHu/$isinit ")
        if (isSanHu&&isinit==1) {
            TBSPreView(fileUrl.toUri(), filePrivater).show(context.supportFragmentManager)
            return true
        }
        return false
    }

    var folioReader: FolioReader? = null
    fun openEpub(context: FragmentActivity, path: String) {
        try {
            folioReader = FolioReader.get()
                .setOnClosedListener {
                    if (folioReader != null) {
                        FolioReader.clear()
                        FolioReader.stop()
                    }
                }
            var config = AppUtil.getSavedConfig(context)
            if (config == null) {
                config = Config()
            }
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL)
            folioReader?.setConfig(config, true)?.openBook(path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 打开视频
     * @param videoUrl 视频地址
     * 视频不做存储，直接展示
     * */
    fun openVideo(context: FragmentActivity, videoUrl: String,fileName: String?) {
//        var uri = Uri.parse(videoUrl);
//        var intent = Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "video/*");
//        context.startActivity(intent);
//        VideoPreView(videoUrl).show(context.supportFragmentManager, null)
        context.startActivity(Intent(context, OpenVideoPlayActivity::class.java ).putExtra("videoUrl",videoUrl).putExtra("fileName",fileName))
        context.overridePendingTransition(0,0)
    }

    /**
     * 打开音频
     * @param videoUrl 音频地址
     * */
    fun openAudio(context: FragmentActivity, videoUrl: String) {
        var uri = Uri.parse(videoUrl);
        var intent = Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/*");
        context.startActivity(intent);
//        VideoPreView(videoUrl.toUri()).show(context.supportFragmentManager, null)
    }

    fun openTxt(context: FragmentActivity, txtUrl: String, savePath: String? = context.filesDir.path) {

        DownLoadUtile.downloadFile(
            context,
            savePath ?: context.filesDir.path,
            txtUrl,
            null,
            object : ICell<File> {
                override fun cell(cell: File) {
                    FileIOUtils.readFile2String(cell).let { text ->
                        TxtPreView(text, null).show(context.supportFragmentManager)
                    }
                }
            })
    }

    fun openHTML(context: FragmentActivity, txtUrl: String) {
        DownLoadUtile.downloadFile(
            context,
            context.filesDir.path,
            txtUrl,
            null,
            object : ICell<File> {
                override fun cell(cell: File) {
                    FileIOUtils.readFile2String(cell).let { text ->
                        TxtPreView(text, 1).show(context.supportFragmentManager)
                    }
                }
            })
    }

    fun openImage(context: FragmentActivity, downUri: String, fileName: String? = "") {
        context.runOnUiThread {
            PreImageDialog(context, mutableListOf(downUri),mutableListOf(fileName), 1).show()
        }
    }

    fun openOther(context: FragmentActivity, downUri: String, filePrivate: String = "") {
        var filePrivater = getfilePrivate(context, filePrivate)
        val isValid = URLUtil.isValidUrl(downUri) && Patterns.WEB_URL.matcher(downUri).matches()
        if (!isValid) {
            try {
                val file = File(downUri)
                val intent = Intent(Intent.ACTION_VIEW)
                val fileUri = FileProvider.getUriForFile(context, filePrivater, file)
                intent.setDataAndType(fileUri, OpenFileUtils.getFileIntentType(file.extension))
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                context.startActivity(intent)
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
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
            .show()
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
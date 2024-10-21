package com.example.openfilelibrary

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.webkit.URLUtil
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.LogUtils
import com.example.openfilelibrary.utile.common.FileType
import com.example.openfilelibrary.utile.common.ImageType
import com.example.openfilelibrary.utile.common.ZipType
import com.example.openfilelibrary.utile.common.getSuffixName1
import com.hjq.toast.Toaster
import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.cache.ICacheManager
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File


/**
 * @author zyju
 * @date 2024/8/20 17:39
 */
object OpenFileUtils {
    private var openFileViewModel = OpenFileViewModel()

    /**
     * @param fileUrl 文件下载地址
     * */
    fun openFile(context: FragmentActivity, fileName: String) {
        openFile(context, context.filesDir.path, fileName, null)
    }

    /**
     * @param fileUrl 文件下载地址
     * @param name 文件名 有时下载地址没有文件名
     * */
    fun openFile(context: FragmentActivity, fileUrl: String, name: String) {
        openFile(context, context.filesDir.path, fileUrl, name)
    }

    fun openFile(context: FragmentActivity, savePath: String?, downUri: String, fileName: String?) {
        try {
            LogUtils.e("OpenFileUtils", "downUri:$downUri fileName:$fileName")
            var suffix = getSuffixName1(downUri)
            if (!fileName.isNullOrBlank()) {
                suffix = getSuffixName1(fileName)
            }
            when (suffix.uppercase()) {
//                FileType.DOCX.name->{}
//                FileType.DOC.name->{}
                FileType.TXT.name -> {
                    openFileViewModel.openTxt(context, downUri)
                }
//                FileType.XLSX.name->{}
//                FileType.XLS.name->{}
//                FileType.PPT.name->{}
                FileType.PDF.name -> {
                    savePath?.let { openFileViewModel.openPDF(context, it, downUri, fileName) }
                }
//
//                ZipType.ZIP.name->{}
//                ZipType.RAR.name->{}
//                ZipType.EXE.name->{}
//                ZipType.ISO.name->{}
                ZipType.HTML.name -> {
                    openFileViewModel.openHTML(context, downUri)
                }

//                ImageType.TIFF.name->{}
                ImageType.JPG.name,
                ImageType.PNG.name,
                ImageType.GIF.name -> {
                    openFileViewModel.openImage(context, downUri)
                }
//                ImageType.BMP.name->{}
//                ImageType.WAV.name->{}
//                ImageType.WMA.name->{}

                ImageType.MP3.name,
                ImageType.MP4.name -> {
                    openFileViewModel.openVideo(context, downUri)
                }
//                ImageType.MOV.name->{}
//                ImageType.MPEG.name->{}
//                ImageType.AVI.name->{}
//                ImageType.FLV.name->{}
                else -> {
                    openOther(context, downUri)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toaster.show("暂不支持此文件类型")
        }

    }

    private fun openOther(context: FragmentActivity, downUri: String) {
        val isValid = URLUtil.isValidUrl(downUri) && Patterns.WEB_URL.matcher(downUri).matches()
        if (!isValid) {
            try {
                val file = File(downUri)
                val intent = Intent(Intent.ACTION_VIEW)
                val fileUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                intent.setDataAndType(fileUri, getFileIntentType(file.extension))
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                context.startActivity(intent)
            } catch (e: Exception) {
                LogUtils.e("openOther", "error:${e.message}")
                Toaster.show("未安装可打开此文件的应用")
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

    fun getFileIntentType(filePath: String?): String {
        if (filePath.isNullOrEmpty()) return ""
        return when (filePath) {
            "wps" -> "application/vnd.ms-works"
            "pdf", "PDF" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "epub" -> "application/epub+zip"

            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "gif" -> "image/gif"
            "htm", "html" -> "text/html"
            "jpeg", "jpg" -> "image/jpeg"
            "png" -> "image/png"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "zip" -> "application/x-zip-compressed"
            "txt" -> "text/plain"
            "mp4", "mpg4" -> "video/mp4"
            else -> ""
        }
    }


}
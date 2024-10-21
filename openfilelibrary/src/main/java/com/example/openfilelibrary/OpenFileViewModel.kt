package com.example.openfilelibrary

import android.util.Patterns
import android.webkit.URLUtil
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.FileIOUtils
import com.example.openfilelibrary.base.ICell
import com.example.openfilelibrary.image.PreImageDialog
import com.example.openfilelibrary.pdf.PDFPreView
import com.example.openfilelibrary.txt.TxtPreView
import com.example.openfilelibrary.utile.common.DownLoadUtile
import com.example.openfilelibrary.video.VideoPreView
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
        val isValid = URLUtil.isValidUrl(downUri) && Patterns.WEB_URL.matcher(downUri).matches()
        if (!isValid) {
            PDFPreView(File(downUri).toUri()).show(context.supportFragmentManager)
            return
        }
        DownLoadUtile.downloadFile(context, savePath, downUri, fileName, object : ICell<File> {
            override fun cell(cell: File) {
                PDFPreView(cell.toUri()).show(context.supportFragmentManager)
            }
        })
    }

    /**
     * 打开视频/音频
     * @param videoUrl 视频地址
     * 视频不做存储，直接展示
     * */
    fun openVideo(context: FragmentActivity, videoUrl: String) {
        VideoPreView(videoUrl.toUri()).show(context.supportFragmentManager, null)
    }

    fun openTxt(context: FragmentActivity, txtUrl: String) {
        val isValid = URLUtil.isValidUrl(txtUrl) && Patterns.WEB_URL.matcher(txtUrl).matches()
        if (!isValid) {
            TxtPreView(txtUrl, null).show(context.supportFragmentManager)
            return
        }
        DownLoadUtile.downloadFile(
            context,
            context.filesDir.path,
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
        val isValid = URLUtil.isValidUrl(txtUrl) && Patterns.WEB_URL.matcher(txtUrl).matches()
        if (!isValid) {
            TxtPreView(txtUrl, 1).show(context.supportFragmentManager)
            return
        }
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

    fun openImage(context: FragmentActivity, downUri: String) {
        PreImageDialog(context, mutableListOf(downUri), 1).show()
    }
}
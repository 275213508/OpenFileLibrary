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
import com.blankj.utilcode.util.SPUtils
import com.example.openfilelibrary.utile.TbsInstance
import com.example.openfilelibrary.utile.common.FileType
import com.example.openfilelibrary.utile.common.ImageType
import com.example.openfilelibrary.utile.common.ZipType
import com.example.openfilelibrary.utile.common.config.mFilePrivateKey
import com.example.openfilelibrary.utile.common.config.tbsLicenseKey
import com.example.openfilelibrary.utile.common.getSuffixName1
import com.hjq.toast.Toaster
import com.lxj.xpopup.XPopup
import com.tencent.tbs.reader.TbsFileInterfaceImpl
import java.io.File


/**
 * @author zyju
 * @date 2024/8/20 17:39
 */
object OpenFileUtils {
    private var openFileViewModel = OpenFileViewModel()

    fun setFilePrivate(filePrivate: String) {
        SPUtils.getInstance().put(mFilePrivateKey, filePrivate)
    }

    fun setTFBLicenseKey(tbsLicenseKeyed: String) {
        SPUtils.getInstance().put(tbsLicenseKey, tbsLicenseKeyed)
    }

    /**
     * @param fileUrl 文件下载地址
     * */
    fun openFile(context: FragmentActivity, fileName: String, filePrivate: String = "") {
        openFile(context, context.filesDir.path, fileName, null, filePrivate)
    }
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
    fun openFile(context: FragmentActivity, fileUrl: String, name: String, filePrivate: String = "") {
        openFile(context, context.filesDir.path, fileUrl, name, filePrivate)
    }

    /**
     * @param savePath 本地路径
     * @param downUri 文件下载地址
     * @param fileName 文件名,后台反馈的下载路径有时没有文件名,识别文件格式需要传文件名, 没有文件名可以传null
     * */
    fun openFile(context: FragmentActivity, savePath: String?, downUri: String, fileName: String?, filePrivate: String = "") {
        try {
            if (!Toaster.isInit()) {
                Toaster.init(context.application)
            }
            LogUtils.e("OpenFileUtils", "downUri:$downUri fileName:$fileName")
            var suffix = getSuffixName1(downUri)
            if (!fileName.isNullOrBlank()) {
                suffix = getSuffixName1(fileName)
            }
            when (suffix.uppercase()) {
                FileType.TXT.name -> {
                    openFileViewModel.openTxt(context, downUri, savePath)
                }

                FileType.DOC.name,
                FileType.DOCX.name,
                FileType.PPT.name,
                FileType.PPTX.name,
                FileType.XLS.name,
                FileType.XLSX.name,
                FileType.CSV.name,
                FileType.DWG.name,
                FileType.CHM.name,
                FileType.RTF.name -> {
                    if (!openFileViewModel.openTBS(context, downUri, filePrivate)) {
                        openFileViewModel.openOther(context, downUri, filePrivate)
                    }
                }

                FileType.PDF.name -> {
                    if (!openFileViewModel.openTBS(context, downUri, filePrivate)) {
                        savePath?.let { openFileViewModel.openPDF(context, it, downUri, fileName) }
                    }
                }

                FileType.EPUB.name -> {
                    openFileViewModel.openEpub(context, downUri)
                }
//
//                ZipType.ZIP.name->{}
//                ZipType.RAR.name->{}
//                ZipType.EXE.name->{}
//                ZipType.ISO.name->{}
                ZipType.HTML.name -> {
                    openFileViewModel.openHTML(context, downUri)
                }

//                ImageType.TIFF.name,
                ImageType.JPG.name,
                ImageType.PNG.name,
//                ImageType.BMP.name,
                ImageType.JPEG.name,
                ImageType.GIF.name -> {
                    openFileViewModel.openImage(context, downUri)
                }
//                ImageType.WAV.name->{}
//                ImageType.WMA.name->{}

                ImageType.MP3.name -> {
                    openFileViewModel.openVideo(context, downUri)
                }

                ImageType.MP4.name -> {
                    openFileViewModel.openVideo(context, downUri)
                }
//                ImageType.MOV.name->{}
//                ImageType.MPEG.name->{}
//                ImageType.AVI.name->{}
//                ImageType.FLV.name->{}
                else -> {
                    var tbsCanOpen = TbsFileInterfaceImpl.canOpenFileExt(getSuffixName1(downUri))
                    if (TbsInstance.getInstance().initEngine(context) == 0 && tbsCanOpen) {
                        openFileViewModel.openTBS(context, downUri, filePrivate)
                    } else {
                        openFileViewModel.openOther(context, downUri, filePrivate)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toaster.show("暂不支持此文件类型")
        }

    }

    fun openOther(context: FragmentActivity, downUri: String, filePrivate: String = "") {
       openFileViewModel.openOther(context, downUri,filePrivate)
    }

    fun getFileIntentType(filePath: String?): String {
        if (filePath.isNullOrEmpty()) return ""
        return when (filePath.lowercase()) {
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
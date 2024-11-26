package com.example.openfilelibrary.utile.common

import android.content.Context
import android.view.View
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SPUtils
import com.example.openfilelibrary.utile.common.config.mFilePrivateKey

/**
 * @author zyju
 * @date 2024/9/5 16:24
 */
internal fun SingleClick(view: View, listener: View.OnClickListener){
    ClickUtils.applyGlobalDebouncing(view,500L,listener)
}

/**
 * 获取文件后缀名 不包括.
 *
 * @param fileName
 */
public fun getSuffixName1(fileName: String): String {
    if (fileName.contains(".")) {
        val names = fileName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return names[names.size - 1]
    } else {
        return ""
    }
}

/**
 * 获取文件名
 *
 * @param fileuri
 */
internal fun getFileName1(fileuri: String): String {
    if (fileuri.contains(".")) {
        val names = fileuri.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return names[names.size - 1]
    } else {
        return ""
    }
}

fun Long.toTimeString(): String {
    val hours = this / (1000 * 60 * 60)
    val minutes = (this % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (this % (1000 * 60)) / 1000
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
fun Int.toTimeString(): String {
   return this.toLong().toTimeString()
}

/**
 * 获取文件的FileProvider
 * */
fun getOpenFilePrivate(context: Context): String {
    var filePrivater = ""
    if (filePrivater.isBlank()) {
        filePrivater = SPUtils.getInstance().getString(mFilePrivateKey, "")
        if (filePrivater.isNullOrBlank()) {
            filePrivater = context.packageName + ".fileprovider"
        }
    }
    return filePrivater
}
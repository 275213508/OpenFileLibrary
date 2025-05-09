package com.example.openfilelibrary.utile.common

import android.content.Context
import android.os.Environment
import android.view.View
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.example.openfilelibrary.utile.common.config.mFilePrivateKey
import java.io.File


/**
 * @author zyju
 * @date 2024/9/5 16:24
 */
internal fun SingleClick(view: View, listener: View.OnClickListener){
    ClickUtils.applyGlobalDebouncing(view,500L,listener)
}
/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/06
 *    desc   : 防重复点击注解
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
annotation class SingleClick constructor(
    /**
     * 快速点击的间隔
     */
    val value: Long = 1000
)
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
/**是否内部存储*/
fun isInternalDirectory(context: Context,file: File): Boolean {
    // 获取内部存储路径
    val internalDir: File = context.getFilesDir()
    val internalPath = internalDir.absolutePath
// 获取外部存储路径
    val externalDir = Environment.getExternalStorageDirectory()
    val externalPath = externalDir.absolutePath
    // 判断文件路径类型
    if (file.getAbsolutePath().startsWith(internalPath)) {
        // 内部存储路径
        LogUtils.d("File Path", "Internal Storage");
        return true
    } else if (file.getAbsolutePath().startsWith(externalPath)) {
        // 外部存储路径
        LogUtils.d("File Path", "External Storage");
        return false
    } else {
        // 其他路径
        LogUtils.d("File Path", "Unknown Storage");
        return false
    }
}
package com.example.openfilelibrary.utile.common

import android.view.View
import com.blankj.utilcode.util.ClickUtils

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
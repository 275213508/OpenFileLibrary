package com.example.openfilelibrary.tbs

import android.content.Context

/**
 * @author zyju
 * @date 2024/8/22 14:33
 */
object TBSUtile {
    fun isAppInstalled(context: Context, packageName: String?): Boolean {
        try {
            val packageManager = context.packageManager // 获取packagemanager
            val packageInfo = packageManager.getInstalledPackages(0) // 获取所有已安装程序的包信息
            if (packageInfo != null) {
                for (i in packageInfo.indices) {
                    val pn = packageInfo[i].packageName
                    if (pn.equals(packageName, ignoreCase = true)) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
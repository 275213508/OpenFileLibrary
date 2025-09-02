package com.example.openfilelibrary.utile.common

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.FileUtils
import com.example.openfilelibrary.base.ICell
import com.permissionx.guolindev.PermissionX
import java.io.File

/**
 * 权限管理工具类，适配Android新版本权限变更
 */
object PermissionManager {
    
    /**
     * 检查是否具有文件读写权限
     */
    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11及以上版本，检查管理外部存储权限
            Environment.isExternalStorageManager()
        } else {
            // Android 11以下版本，检查读写权限
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * 请求存储权限
     * @param activity 上下文
     * @param needManageStorage 是否需要管理外部存储权限(Android 11+)
     * @param callback 权限请求结果回调
     */
    fun requestStoragePermission(
        activity: FragmentActivity,
        needManageStorage: Boolean = true,
        callback: ICell<Boolean>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && needManageStorage) {
            // Android 11及以上版本，需要请求管理外部存储权限
            if (Environment.isExternalStorageManager()) {
                callback.cell(true)
            } else {
                // 引导用户到设置页面开启权限
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:${activity.packageName}")
                activity.startActivity(intent)
                callback.cell(false)
            }
        } else {
            // Android 11以下版本或其他情况，使用PermissionX请求权限
            PermissionX.init(activity)
                .permissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(
                        deniedList,
                        "应用需要存储权限来下载和访问文件",
                        "确定",
                        "取消"
                    )
                }
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        "请在设置中手动授予存储权限，以正常使用文件功能",
                        "前往设置",
                        "取消"
                    )
                }
                .request { allGranted, _, _ ->
                    callback.cell(allGranted)
                }
        }
    }
    
    /**
     * 检查文件是否可访问
     * @param context 上下文
     * @param file 文件
     * @return 是否可访问
     */
    fun isFileAccessible(context: Context, file: File): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11及以上版本，检查是否具有管理外部存储权限或文件在应用私有目录下
            return Environment.isExternalStorageManager() || 
                   isInternalDirectory(context, file) ||
                   FileUtils.isDir(file.parentFile) // 检查父目录是否存在
        } else {
            // Android 11以下版本，检查权限和文件是否存在
            return hasStoragePermission(context) && file.exists()
        }
    }
    
    /**
     * 判断文件是否在应用内部存储目录
     * @param context 上下文
     * @param file 文件
     * @return 是否在内部存储目录
     */
    fun isInternalDirectory(context: Context, file: File): Boolean {
        // 获取内部存储路径
        val internalDir: File = context.filesDir
        val internalPath = internalDir.absolutePath
        
        // 获取外部存储路径
        val externalDir = context.getExternalFilesDir(null)
        val externalPath = externalDir?.absolutePath ?: ""
        
        // 判断文件路径类型
        return when {
            file.absolutePath.startsWith(internalPath) -> {
                // 内部存储路径
                true
            }
            file.absolutePath.startsWith(externalPath) -> {
                // 应用外部存储路径
                true
            }
            else -> {
                // 其他路径
                false
            }
        }
    }
    
    /**
     * 获取适合当前Android版本的文件存储目录
     * @param context 上下文
     * @param subDir 子目录名
     * @return 文件存储目录
     */
    fun getSuitableStorageDir(context: Context, subDir: String = "files"): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11及以上版本，使用应用专属外部存储目录
            val externalDir = context.getExternalFilesDir(null)
            File(externalDir, subDir)
        } else {
            // Android 11以下版本，可以使用外部存储根目录
            val externalDir = Environment.getExternalStorageDirectory()
            File(externalDir, "OpenFileLibrary/$subDir")
        }
    }
}
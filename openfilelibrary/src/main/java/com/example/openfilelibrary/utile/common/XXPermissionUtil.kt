package com.example.openfilelibrary.utile.common

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.example.openfilelibrary.base.ICell
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback

public class XXPermissionUtil {

    fun requestPermission(context: FragmentActivity, permission: String, callback: RequestCallback) {
        PermissionX.init(context).permissions(permission)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "使用App需要进行授权", "授权", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList.filter { it == WRITE_EXTERNAL_STORAGE || it == READ_EXTERNAL_STORAGE },
                    "您需要在“设置”中手动授予必要的权限,以正常使用本软件",
                    "前往",
                    "取消"
                )
            }
            .request(callback)
//        XXPermissions.with(context)
//            // 申请单个权限
//            .permission(permission)
//            // 申请多个权限
////            .permission(Permission.Group.CALENDAR)
//            // 设置权限请求拦截器（局部设置）
//            //.interceptor(new PermissionInterceptor())
//            // 设置不触发错误检测机制（局部设置）
//            //.unchecked()
//            .request(callback)
    }


    fun requestPermission(
        context: FragmentActivity,
        permission: List<String>,
        permissionString: StringBuilder,
        callback: RequestCallback
    ) {
        PermissionX.init(context).permissions(*permission.toTypedArray())
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, permissionString.toString(), "授权", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "您需要在“设置”中手动授予必要的权限\n${permissionString}",
                    "前往",
                    "取消"
                )
            }
            .request(callback)
    }

    companion object {
        /**
         * @param isInit 是否第一次加载, 第一次加载需要申请权限
         * */
        fun requestWritePermission(context: FragmentActivity, isInit: Boolean, iCell: ICell<Boolean>? = null) {
            if (!isInit) {
                iCell?.cell(false)
                return
            }
            // 2023/8/24 [领航卡重构]
            var needPermission = arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
            var permissionString = StringBuilder()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                needPermission = arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                if (!PermissionX.isGranted(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                    permissionString.append("需要使用本地文件存储,请允许读写权限")
                }
            }
            if (!PermissionX.isGranted(context, WRITE_EXTERNAL_STORAGE)) {
                permissionString.append("需要使用本地文件存储,请允许读写权限")
            }
            if (permissionString.contains("需要使用本地文件存储,请允许读写权限需要使用本地文件存储,请允许读写权限")) {
                permissionString.clear()
                permissionString.append("需要使用本地文件存储,请允许读写权限")
            }
            XXPermissionUtil().requestPermission(context, needPermission.toList(), permissionString) { allGranted, grantedList, deniedList -> iCell?.cell(allGranted) }

        }

        fun isGrantedFromWrite(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                PermissionX.isGranted(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            } else {
                PermissionX.isGranted(context, WRITE_EXTERNAL_STORAGE)
            }
        }

        fun getWritePermission(context: Context): ArrayList<String> {
            var permission = ArrayList<String>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!PermissionX.isGranted(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                    permission.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                }
            } else {
                if (!PermissionX.isGranted(context, WRITE_EXTERNAL_STORAGE)) {
                    permission.add(WRITE_EXTERNAL_STORAGE)
                }
                if (!PermissionX.isGranted(context, READ_EXTERNAL_STORAGE)) {
                    permission.add(READ_EXTERNAL_STORAGE)
                }
            }
            return permission
        }

    }
}
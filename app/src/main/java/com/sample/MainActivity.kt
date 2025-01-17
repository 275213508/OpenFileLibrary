package com.sample

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_act)
        replaceFragment()
//        startActivity(Intent(this, GSYVideoViewDialog::class.java))
    }

    private fun replaceFragment() {
        PermissionX.init(this).permissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        ).onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(deniedList, "需要权限", "确认", "取消")
        }.request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, MainFragment())
                    .commit()
            }
        }
    }

}
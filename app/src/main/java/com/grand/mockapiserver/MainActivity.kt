package com.grand.mockapiserver

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.grand.mockapiserver.core.MockWebServerService
import java.security.Permission
import java.security.Permissions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, MockWebServerService::class.java))
        } else {
            startService(Intent(this, MockWebServerService::class.java))
        }

        var check = false
        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                check = true
                return@forEach
            }
        }
        if (check)
            ActivityCompat.requestPermissions(this, permissions, 12)
    }

    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
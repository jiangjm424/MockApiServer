package com.grand.mockapiserver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.grand.mockapiserver.core.MockWebServerService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.i("jiang","start foreground service")
            startForegroundService(Intent(this, MockWebServerService::class.java))
        } else {
            Log.i("jiang","start service")
            startService(Intent(this, MockWebServerService::class.java))
        }
    }
}
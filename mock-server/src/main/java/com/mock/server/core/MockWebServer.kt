package com.mock.server.core

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson


class MockWebServer : Service() {

    companion object {
        private const val TAG = "MockWebServer"
    }
    private lateinit var appMockWebServer: AbsMockServer

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "onCreate .......")
        appMockWebServer = DefaultMockServer(this, Gson())
        appMockWebServer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy .......")
        appMockWebServer.stop()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}

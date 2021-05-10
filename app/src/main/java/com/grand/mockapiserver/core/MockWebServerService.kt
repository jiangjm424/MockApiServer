package com.grand.mockapiserver.core

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.grand.mockapiserver.MainActivity
import com.grand.mockapiserver.R
import com.grand.mockapiserver.core.AbsMockServer.Companion.TAG


class MockWebServerService : Service() {

    private lateinit var appMockWebServer: AbsMockServer

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "onCreate .......")
        appMockWebServer = DefaultMockServer(this, Gson())
        appMockWebServer.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(application, 0, activityIntent, 0)
        val builder: Notification.Builder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = createNotificationChannel("my_service", "My Background Service")
            Notification.Builder(this, channelId)
        } else {
            Notification.Builder(this)
        }
        builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("MockWebServer")
                .setContentText("Mock Web server Running...")
                .setContentIntent(pendingIntent)
        val notification = builder.build()
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
                channelId,
                channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onBind(intent: Intent?): IBinder? {
//        TODO("Not yet implemented")
        return null
    }

}
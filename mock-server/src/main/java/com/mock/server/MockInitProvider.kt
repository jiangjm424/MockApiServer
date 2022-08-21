package com.mock.server

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.IBinder
import com.mock.server.core.MockWebServer

class MockInitProvider : ContentProvider() {
    override fun onCreate(): Boolean {

        context?.bindService(Intent(context, MockWebServer::class.java), object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }, Context.BIND_AUTO_CREATE)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}

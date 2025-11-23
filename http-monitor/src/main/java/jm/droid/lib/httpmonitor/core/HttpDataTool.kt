package jm.droid.lib.httpmonitor.core

import android.content.Context
import android.util.Log
import jm.droid.lib.httpmonitor.db.HttpDataDao
import jm.droid.lib.httpmonitor.db.HttpDataDb

internal object HttpDataTool {
    lateinit var httpDataDao: HttpDataDao
    fun init(context: Context) {
        Log.i("HttpMonitor", "http monitor init with:${context.packageName}")
        httpDataDao = HttpDataDb.get(context).httpDao()
    }
}

package jm.droid.lib.httpmonitor.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun formatTime(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.getDefault()).format(date)
    return format.format(date)
}

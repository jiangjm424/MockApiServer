package jm.droid.lib.httpmonitor.db

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

class HttpSimpleData {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "method")
    var method: String? = null

    @ColumnInfo(name = "host")
    var host: String? = null

    @ColumnInfo(name = "path")
    var path: String? = null

    @ColumnInfo(name = "responseCode")
    var responseCode = 0

    @ColumnInfo(name = "requestTime")
    var requestTime: Long? = null

    @ColumnInfo(name = "duration")
    var duration: Long? = null
}

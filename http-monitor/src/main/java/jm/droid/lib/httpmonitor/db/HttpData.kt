package jm.droid.lib.httpmonitor.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_http_monitor_data")
class HttpData {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "method")
    var method: String? = null

    @ColumnInfo(name = "scheme")
    var scheme: String? = null

    @ColumnInfo(name = "host")
    var host: String? = null

    @ColumnInfo(name = "path")
    var path: String? = null

    @ColumnInfo(name = "protocol")
    var protocol: String? = null

    @ColumnInfo(name = "url")
    var url: String? = null

    @ColumnInfo(name = "requestBody")
    var requestBody: ByteArray? = null

    @ColumnInfo(name = "requestBodyDesc")
    var requestBodyDesc: String? = null

    @ColumnInfo(name = "requestContentLength")
    var requestContentLength: Long? = null

    @ColumnInfo(name = "requestContentType")
    var requestContentType: String? = null

    @ColumnInfo(name = "requestHeaders")
    var requestHeaders: String? = null

    @ColumnInfo(name = "errorMsg")
    var errorMsg: String? = null

    @ColumnInfo(name = "exception")
    var exception: String? = null

    @ColumnInfo(name = "responseBody")
    var responseBody: ByteArray? = null

    @ColumnInfo(name = "responseBodyDesc")
    var responseBodyDesc: String? = null

    @ColumnInfo(name = "responseCode")
    var responseCode = 0

    @ColumnInfo(name = "responseContentLength")
    var responseContentLength: Long? = null

    @ColumnInfo(name = "responseContentType")
    var responseContentType: String? = null

    @ColumnInfo(name = "responseHeaders")
    var responseHeaders: String? = null

    @ColumnInfo(name = "requestTime")
    var requestTime: Long? = null

    @ColumnInfo(name = "responseTime")
    var responseTime: Long? = null

    @ColumnInfo(name = "duration")
    var duration: Long? = null
}

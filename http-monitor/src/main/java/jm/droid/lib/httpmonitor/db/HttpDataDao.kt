package jm.droid.lib.httpmonitor.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HttpDataDao {
    @Query("SELECT * FROM t_http_monitor_data ORDER BY id DESC")
    fun getHttpDatas(): List<HttpData>

    @Query("SELECT id, requestTime, method, responseCode, duration, path, host FROM t_http_monitor_data ORDER BY id DESC")
    fun getSimpleHttpDataList(): List<HttpSimpleData>

    @Query("SELECT * FROM t_http_monitor_data WHERE id = :httpId")
    fun getHttpData(httpId: Long): HttpData

    @Insert(entity = HttpData::class, onConflict = OnConflictStrategy.REPLACE)
    fun putHttpData(httpData: HttpData)

    @Query("DELETE FROM t_http_monitor_data")
    fun clear()

}

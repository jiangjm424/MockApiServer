package jm.droid.lib.httpmonitor.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HttpData::class], version = 1, exportSchema = false)
abstract class HttpDataDb : RoomDatabase() {
    abstract fun httpDao(): HttpDataDao

    companion object {
        private const val DB_NAME = "http_monitor.db"
        private var instance: HttpDataDb? = null
        fun get(context: Context): HttpDataDb {
            if (instance == null) {
                synchronized(HttpDataDb::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            HttpDataDb::class.java, DB_NAME
                        ).build()
                    }
                }
            }
            return instance!!
        }
    }
}

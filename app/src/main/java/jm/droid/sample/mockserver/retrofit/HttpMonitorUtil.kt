package jm.droid.sample.mockserver.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient

object HttpMonitorUtil {
    private const val clazz_http_monitor = "jm.droid.lib.httpmonitor.core.HttpMonitorInterceptor"
    private val supported = try {
        Class.forName(clazz_http_monitor)
        true
    } catch (ex: Exception) {
        false
    }

    fun httpMonitor(builder: OkHttpClient.Builder) {
        if (supported) {
            val interceptor = Class.forName(clazz_http_monitor).newInstance() as Interceptor
            builder.addInterceptor(interceptor)
        }
    }
}

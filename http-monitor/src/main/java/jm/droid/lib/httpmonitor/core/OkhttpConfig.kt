package jm.droid.lib.httpmonitor.core

import okhttp3.OkHttpClient

object OkhttpConfig {
    fun httpMonitor(builder: OkHttpClient.Builder) {
        val interceptors = builder.interceptors().filterIsInstance<HttpMonitorInterceptor>()
        if (interceptors.isEmpty()) {
            builder.addInterceptor(HttpMonitorInterceptor())
        }
    }
}

package jm.droid.sample.mockserver.retrofit

import okhttp3.OkHttpClient


object MockServerTool {
    private const val class_name = "jm.droid.lib.mock.server.MockServerDebug"
    private val supported = try {
        Class.forName(class_name)
        true
    } catch (ex: Exception) {
        false
    }

    fun config(builder: OkHttpClient.Builder) = builder.takeIf { supported }?.apply {
        try {
            val clazz = Class.forName(class_name)
            val configOkHttpClient =
                clazz.getMethod("configOkHttpClient", OkHttpClient.Builder::class.java).apply {
                    isAccessible = true
                }
            val mockServerDebug = clazz.newInstance()
            configOkHttpClient.invoke(mockServerDebug, builder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

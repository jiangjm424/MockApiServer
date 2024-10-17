package jm.droid.lib.mock.server.core

import okhttp3.Interceptor
import okhttp3.Response
import java.net.InetAddress

class HostReplaceIntercept:Interceptor {
    companion object {
        private const val HEADER_MOCK = "mock"
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val bMock = "true" == request.headers[HEADER_MOCK]
        val newRequest = if (bMock) {
            val uri = request.url.newBuilder().apply {
                host(InetAddress.getLocalHost().canonicalHostName)
                scheme("https")
                port(6878)
            }.build()
            val builder = request.newBuilder().url(uri)
            builder.build()
        } else {
            request
        }
        return chain.proceed(newRequest)
    }
}

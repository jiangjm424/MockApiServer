package jm.droid.lib.mock.server

import android.annotation.SuppressLint
import jm.droid.lib.mock.server.core.HostReplaceIntercept
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class MockServerDebug {
    private val unsafeTrustAllCerts = arrayOf(DebugX509TrustManager())
    fun configOkHttpClient(builder: OkHttpClient.Builder) = builder.apply {
        sslSocketFactory(createUnsafeSocketFactory(), unsafeTrustAllCerts[0])
        hostnameVerifier { _, _ -> true }
        addInterceptor(HostReplaceIntercept())
    }

    private fun createUnsafeSocketFactory(): SSLSocketFactory {
        // Create a trust manager that does not validate certificate chains
        // Install the all-trusting trust manager
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, unsafeTrustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        return sslContext.socketFactory
    }

    @SuppressLint("CustomX509TrustManager")
    private class DebugX509TrustManager : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }
}

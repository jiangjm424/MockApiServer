package com.grand.mockapiserver.core

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import java.net.InetAddress
import java.util.concurrent.CountDownLatch

@Deprecated(message = "no use again", replaceWith = ReplaceWith("user AbsMockServer instead"))
class MockWebServerInApp2
constructor(
    val context: Context,
    val gson: Gson,
) {
    private val server = MockWebServer()
    private val localhost: HandshakeCertificates by lazy {
        // Generate a self-signed cert for the server to serve and the client to trust.
        val heldCertificate = HeldCertificate.Builder()
            .commonName("localhost")
            .addSubjectAlternativeName(InetAddress.getByName("localhost").canonicalHostName)
            .build()
        return@lazy HandshakeCertificates.Builder()
            .heldCertificate(heldCertificate)
            .addTrustedCertificate(heldCertificate.certificate)
            .build()
    }


    fun start() {
        server.dispatcher = RequestDispatcher(context)
        val countDownLatch = CountDownLatch(1)
        GlobalScope.launch(Dispatchers.IO) {
            val handshakeCertificates = localhost
            server.useHttps(handshakeCertificates.sslSocketFactory(), false)
            server.start(InetAddress.getLocalHost(), 6878)
            val url = server.url("/")
            countDownLatch.countDown()
        }
        countDownLatch.await()
    }

    fun getBaseUrl(): String {
        return "https://localhost:6878/"
    }
}
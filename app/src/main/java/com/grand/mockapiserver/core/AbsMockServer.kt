package com.grand.mockapiserver.core

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import java.net.InetAddress
import java.util.concurrent.CountDownLatch

abstract class AbsMockServer(protected val context: Context, protected val gson: Gson) : Dispatcher() {
    companion object {
        const val TAG = "AbsMockServer"
    }

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

    private val requestCount = mutableMapOf<String, Int>()
    fun start() {
        server.dispatcher = this
        val countDownLatch = CountDownLatch(1)
        GlobalScope.launch(Dispatchers.IO) {
            val handshakeCertificates = localhost
            server.useHttps(handshakeCertificates.sslSocketFactory(), false)
            server.start(InetAddress.getLocalHost(), 6878)
            val url = server.url("/")
            countDownLatch.countDown()
        }
        Log.i(TAG, "start mock server:$this")
        countDownLatch.await()
        Log.i(TAG, "start done mock server:$this")
    }

    fun stop() {
        server.shutdown()
        Log.i(TAG, "finish mock server:$this")
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        Log.v(TAG, "got request ${request.path}")
        Thread.sleep(1 * 1000)
        requestCount[request.path!!] = (requestCount[request.path!!] ?: 0) + 1
        val fileName = request.path?.let {
            it.substring(it.lastIndexOf("/") + 1) + ".json"
        }
        return configResponse(fileName, requestCount[request.path!!] ?: 0)
                ?: MockResponse().setResponseCode(404)
    }

    abstract fun configResponse(fileName: String?, count: Int): MockResponse?
}
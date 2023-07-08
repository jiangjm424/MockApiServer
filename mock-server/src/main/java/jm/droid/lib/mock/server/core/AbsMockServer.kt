/*
 * Copyright 2022 The Jmdroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jm.droid.lib.mock.server.core

import android.content.Context
import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import okio.Buffer
import java.net.InetAddress
import java.util.concurrent.CountDownLatch

abstract class AbsMockServer(protected val context: Context) : Dispatcher() {
    companion object {
        private const val TAG = "AbsMockServer"
        const val TYPE_PB = "application/x-protobuf;charset=UTF-8"
        const val TYPE_GSON = "application/json; charset=utf-8"
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

    private var serverRunning = false
    private val serverRunnable = object : Runnable {
        override fun run() {
            while (serverRunning) {
                Thread.sleep(10000)
                Log.i(TAG, "$this is running")
            }
        }
    }
    private val requestCount = mutableMapOf<String, Int>()
    fun start() {
        serverRunning = true
        server.dispatcher = this
        val countDownLatch = CountDownLatch(1)
        Thread {
            val handshakeCertificates = localhost
            server.useHttps(handshakeCertificates.sslSocketFactory(), false)
            server.start(InetAddress.getLocalHost(), 6878)
            countDownLatch.countDown()
        }.start()
        Log.i(TAG, "start mock server:$this")
        countDownLatch.await()
        Log.i(TAG, "start done mock server:$this")
        Thread(serverRunnable, "mock_monitor").start()
    }

    fun stop() {
        server.shutdown()
        serverRunning = false
        Log.i(TAG, "finish mock server:$this")
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        Log.v(TAG, "got request ${request.path}")
        Thread.sleep(1 * 1000)
        requestCount[request.path!!] = (requestCount[request.path!!] ?: 0) + 1
        val fileName = request.path?.let {
            it.substring(it.lastIndexOf("/") + 1)
        }
        return mockResponse(request.path!!, request.body)
            ?: configResponse(
                fileName,
                requestCount[request.path!!] ?: 0,
            )
            ?: MockResponse().setResponseCode(404)
    }

    // 读文件的方案
    abstract fun configResponse(fileName: String?, count: Int): MockResponse?

    // 使用mock库直接在运行生成
    abstract fun mockResponse(path: String, param: Buffer): MockResponse?
}

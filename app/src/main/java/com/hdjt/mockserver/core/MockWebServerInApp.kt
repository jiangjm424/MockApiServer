package com.hdjt.mockserver.core

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
import org.json.JSONObject
import java.net.InetAddress
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.CountDownLatch


/**
 * 模拟后台服务响应，并反应json数据
 *
 * 可以查看configResponse*的几个函数作用例子
 */
class MockWebServerInApp
constructor(
    val context: Context,
    val gson: Gson,
) {
    companion object {
        private const val TAG = "AppMockWebServer"
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

    val requestCount = mutableMapOf<String, Int>()
    fun start() {
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                Log.v(TAG, "got request ${request.path}")
                Thread.sleep(1 * 1000)
                requestCount[request.path!!] = (requestCount[request.path!!] ?: 0) + 1
                when {
                    request.path!!.endsWith("/getTopPage") -> {
//                        return if (count <= 2) {
                        return configResponse("card_all_for_test.json")
//                        } else {
//                            Thread.sleep(1 * 1000)
//                            configResponse("card_all_for_test2.json")
//                        }
                    }

                    request.path!!.endsWith("/browser_car") ->
                        return configResponse("browserCars.json")
                    request.path!!.endsWith("/sendComment") ->
                        return configResponseStr("\"data\":0,")
                    request.path!!.endsWith("/getGCAppCardMoreData") -> {
                        val buffer = request.body
                        val str = buffer.readString(UTF_8)
                        val obj = JSONObject(str)
                        val style = obj.getJSONObject("body").getInt("style")
                        if (style == 70003) {
                            return configResponse("video_list_editor.json")
                        } else if (style == 70004) {
                            return configResponse("comment_list.json")
                        } else if (style == 70005) {
                            return configResponse("credits_video_list.json")
                        } else if (style == 70006) {
                            return configResponse("news_list.json")
                        }
                        return configResponse("video_list_app.json")
                    }
                }
                return MockResponse().setResponseCode(404)
            }
        }
        server.dispatcher = dispatcher
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

    private fun configResponse(fileName: String): MockResponse {
        return MockResponse().setResponseCode(200)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Cache-Control", "no-cache")
            .setBody(buildResponse(fileName))
    }

    private fun configResponseStr(dataStr: String): MockResponse {
        return MockResponse().setResponseCode(200)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Cache-Control", "no-cache")
            .setBody(buildResponseStr(dataStr))
    }

    private fun buildResponse(fileName: String): String {
        val configStr = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val templateStr =
            context.assets.open("resp_template.json").bufferedReader().use { it.readText() }
        return templateStr.replace("TEMPLATE_DATA", "\"data\":$configStr,")
    }

    private fun buildResponseStr(dataStr: String): String {
        val templateStr =
            context.assets.open("resp_template.json").bufferedReader().use { it.readText() }
//        "data": TEMPLATE_DATA
        return templateStr.replace("TEMPLATE_DATA", dataStr)
    }

    private fun buildObjResponse(tmpStr: String): MockResponse {
        val templateStr =
            context.assets.open("testdata/resp_template.json").bufferedReader()
                .use { it.readText() }
        val str = templateStr.replace("TEMPLATE_DATA", tmpStr)
        return MockResponse().setResponseCode(200)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Cache-Control", "no-cache")
            .setBody(str)
    }

}
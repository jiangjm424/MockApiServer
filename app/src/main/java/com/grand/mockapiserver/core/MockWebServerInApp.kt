package com.grand.mockapiserver.core

import android.content.Context
import android.os.Environment
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
import java.io.File
import java.net.InetAddress
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.CountDownLatch


@Deprecated(message = "no use again", replaceWith = ReplaceWith("user AbsMockServer instead"))
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
                    //模拟分页加载
                    request.path!!.endsWith("/getTopPage") -> {
                        val count = requestCount[request.path!!] ?: 0
                        return if (count <= 2) {
                            return configResponse("card_all_for_test.json")
                        } else {
                            Thread.sleep(1 * 1000)
                            configResponse("card_all_for_test2.json")
                        }
                    }

                    request.path!!.endsWith("/browser_car") ->
                        return configResponse("browserCars.json")

                    //模拟从请求中解析出参数并使用
                    request.path!!.endsWith("/getCreditsTopPage") -> {
                        val buffer = request.body
                        val str = buffer.readString(UTF_8)
                        val obj = JSONObject(str)
                        val pageId = obj.getJSONObject("body").getInt("pageId")
                        when (pageId) {
                            4 -> {
                                return configResponse("credit_json_top_page_consume_credits.json")
                            }
                            5 -> {
                                return configResponse("credit_json_top_page_get_welfare.json")
                            }
                            else -> return configResponse("credit_json_top_page.json")
                        }
                    }

                }
                return configLocalFileResponse(request.path!!)
                    ?: MockResponse().setResponseCode(404)
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

    /**
     * 这里面输入请求的全路径，我们过滤到最后一作为文件名
     * 此文件会放到手机的download/mock/目录下
     * eg:
     * https://localhost:6878/v1/home/top
     * 则我们会在手机上加载download/mock/top.json的内容
     */
    private fun configLocalFileResponse(path: String): MockResponse? {
        val fileName = path.substring(path.lastIndexOf("/") + 1) + ".json"
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "mock" + File.separator + fileName
        )
        Log.i(TAG,"path: $path, file:${file.absoluteFile}")
        return respFromFile(file)
    }

    private fun respFromFile(file: File): MockResponse? {
        file.takeIf {
            it.exists()
        }?.also { f ->
            val resp = f.bufferedReader().use { reader -> reader.readText() }
            val template =
                context.assets.open("resp_template.json").bufferedReader().use { reader -> reader.readText() }
            val body = template.replace("TEMPLATE_DATA", "\"data\":$resp,")

            return MockResponse().setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody(body)
        }
        Log.i(TAG,"${file.name} not exit, return null file")
        return null
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
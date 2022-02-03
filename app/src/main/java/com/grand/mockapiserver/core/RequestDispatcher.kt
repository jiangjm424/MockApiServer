package com.grand.mockapiserver.core

import android.content.Context
import android.os.Environment
import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.File
@Deprecated(message = "no use again, it used in MockWebServerInApp", replaceWith = ReplaceWith("user AbsMockServer instead"))
class RequestDispatcher(
        private val context: Context,
        private val responseBuilder: ResponseBuilder = DefaultResponseBuild(context)
) :
    Dispatcher() {
    companion object {
        private const val TAG = "RequestDispatcher"
    }

    interface ResponseBuilder {
        fun buildResponse(req: RecordedRequest): MockResponse
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        return responseBuilder.buildResponse(request)
    }

    private class DefaultResponseBuild(val context: Context) : ResponseBuilder {
        val requestCount = mutableMapOf<String, Int>()  //记录每个接口的访问次数
        override fun buildResponse(req: RecordedRequest): MockResponse {
            Log.v(TAG, "got request ${req.path}")
            Thread.sleep(1 * 1000)
            requestCount[req.path!!] = (requestCount[req.path!!] ?: 0) + 1
            return configLocalFileResponse(req.path!!) ?: MockResponse().setResponseCode(404)
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
            val file = File(context.getExternalFilesDir(""),
                "mock" + File.separator + fileName
            )
            Log.i(TAG, "path: $path, file:${file.absoluteFile}")
            return respFromFile(file)
        }

        private fun respFromFile(file: File): MockResponse? {
            file.takeIf {
                it.exists()
            }?.also { f ->
                val resp = f.bufferedReader().use { reader -> reader.readText() }

                return MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody(resp)
            }
            Log.i(TAG, "${file.name} not exit, return null file")
            return null
        }
    }

}
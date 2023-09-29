package jm.droid.lib.httpmonitor.core

import jm.droid.lib.httpmonitor.db.HttpData
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class HttpMonitorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val httpData = HttpData()
        val request = chain.request()
        val requestBody = request.body
        val connection = chain.connection()
        httpData.method = request.method
        httpData.url = request.url.toString()
        httpData.protocol = connection?.protocol().toString()
        httpData.scheme = request.url.scheme
        httpData.host = request.url.host
        httpData.path = request.url.encodedPath
        requestBody?.let { reqBody ->
            httpData.requestContentLength = reqBody.contentLength()
            httpData.requestHeaders = request.headers.joinToString()
            reqBody.contentType()?.let {
                if (request.headers["Content-Type"] == null) {
                    httpData.requestContentType = it.toString()
                }
            }
            val requestBuffer = Buffer()
            requestBody.writeTo(requestBuffer)
            if (bodyHasUnknownEncoding(request.headers)) {
                httpData.requestBodyDesc = "encoded body omitted"
            } else if (requestBody.isDuplex()) {
                httpData.requestBodyDesc = "duplex request body omitted"
            } else if (requestBody.isOneShot()) {
                httpData.requestBodyDesc = "one-shot body omitted"
            } else {
                val contentType = requestBody.contentType()
                val charset: Charset =
                    contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
                if (requestBuffer.isProbablyUtf8()) {
                    httpData.requestBodyDesc = requestBuffer.clone().readString(charset)
                    httpData.requestContentLength = requestBody.contentLength()
                } else {
                    httpData.requestBodyDesc =
                        "binary ${requestBody.contentLength()}-byte body omitted"
                    httpData.requestContentLength = requestBody.contentLength()
                }
            }
            httpData.requestBody = requestBuffer.readByteArray()
        }

        val startNs = System.nanoTime()
        httpData.requestTime = System.currentTimeMillis()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            httpData.exception = "HTTP FAILED: $e"
            HttpDataTool.httpDataDao.putHttpData(httpData)
            throw e
        }
        httpData.responseCode = response.code
        httpData.errorMsg = response.message
        httpData.responseTime = System.currentTimeMillis()

        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()
        val headers = response.headers
        httpData.responseHeaders = headers.joinToString()
        val source = responseBody.source()
        try {
            source.request(Long.MAX_VALUE) // Buffer the entire body. may be timeout
        } catch (e: Exception) {
            httpData.exception = "HTTP FAILED: $e"
            HttpDataTool.httpDataDao.putHttpData(httpData)
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        httpData.duration = tookMs

        var responseBuffer = source.buffer
        httpData.responseContentLength = responseBuffer.size
        httpData.responseBody = responseBuffer.clone().readByteArray()
        if (!response.promisesBody()) {
        } else if (bodyHasUnknownEncoding(response.headers)) {
            httpData.responseBodyDesc = "encoded body omitted"
        } else {
            if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                GzipSource(responseBuffer.clone()).use { gzippedResponseBody ->
                    responseBuffer = Buffer()
                    responseBuffer.writeAll(gzippedResponseBody)
                }
            }
            val contentType = responseBody.contentType()
            httpData.responseContentType = contentType.toString()
            val charset: Charset =
                contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
            if (!responseBuffer.isProbablyUtf8()) {
                httpData.responseBodyDesc = "binary ${responseBuffer.size}-byte body omitted"
            } else if (contentLength != 0L) {
                httpData.responseBodyDesc = responseBuffer.clone().readString(charset)
            }
        }
        HttpDataTool.httpDataDao.putHttpData(httpData)
        return response
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
            !contentEncoding.equals("gzip", ignoreCase = true)
    }

    private fun Buffer.isProbablyUtf8(): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = size.coerceAtMost(64)
            copyTo(prefix, 0, byteCount)
            for (i in 0 until 16) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (_: EOFException) {
            return false // Truncated UTF-8 sequence.
        }
    }
}

package com.mock.server.core

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import okhttp3.mockwebserver.MockResponse
import okio.Buffer
import org.apache.commons.io.IOUtils
import java.io.File

/**
 * 模拟服务响应包数据的创建
 *
 * 默认情况下该类会去读取/sdcard/Android/data/package_name/files/mock/XXX.json并返回
 *
 * @constructor
 */
class DefaultMockServer(context: Context, gson: Gson = Gson()) : AbsMockServer(context, gson) {

    override fun configResponse(fileName: String?, count: Int): MockResponse? {
        val gsonFile = File(context.getExternalFilesDir(""), "mock" + File.separator + fileName + ".json")
        if (gsonFile.exists()) {
            return respFromJsonFile(gsonFile)
        }
        val pbFile = File(context.getExternalFilesDir(""), "mock" + File.separator + fileName + ".pb")
        if (pbFile.exists()) {
            return respFromProto(pbFile)
        }
        return null
    }

    private fun respFromProto(file: File): MockResponse? {
        if (!file.exists()) return null
        val aab = file.inputStream().use {
            IOUtils.toByteArray(it)
        }
        return Buffer().use {
            it.write(aab)
            MockResponse().setResponseCode(200)
                .addHeader("Content-Type", "application/x-protobuf;charset=UTF-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody(it)
        }
    }

    private fun respFromJsonFile(file: File): MockResponse? {
        file.takeIf {
            it.exists()
        }?.also { f ->
            val resp = f.bufferedReader().use { reader -> reader.readText() }
            return MockResponse().setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody(resp)
        }
        return null
    }
}

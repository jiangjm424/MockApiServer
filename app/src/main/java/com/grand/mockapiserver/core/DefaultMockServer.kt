package com.grand.mockapiserver.core

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
 * 另外，该类默认会先查看/sdcard/Android/data/package_name/files/mock/_template.json
 * 是否存在，如果存在则会以这个文件为模板，将XXX.json的文件内容替换掉template.json里面的TEMPLATE_DATA
 * @constructor
 */
class DefaultMockServer(context: Context, gson: Gson = Gson()) : AbsMockServer(context, gson) {

    override fun configResponse(fileName: String?, count: Int): MockResponse? {
        val gsonFile =
            File(context.getExternalFilesDir(""), "mock" + File.separator + fileName + ".json")
        if (gsonFile.exists()) {
            return respFromJsonFile(gsonFile)
        }
        val pbFile =
            File(context.getExternalFilesDir(""), "mock" + File.separator + fileName + ".pb")
        if (pbFile.exists()) {
            return respFromProto(pbFile)
        }
        return null
    }

    private fun respFromProto(file: File): MockResponse? {
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
            val template = readTemplateOrNull()
            val body = template?.replace("TEMPLATE_DATA", "$resp") ?: resp
            return MockResponse().setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody(body)
        }
        Log.i(TAG, "${file.name} not exit, return null file")
        return null
    }

    private fun readTemplateOrNull(): String? {
        val file = File(
            context.getExternalFilesDir(""),
            "mock" + File.separator + "_template.json"
        )
        file.takeIf { it.exists() }?.also {
            return it.bufferedReader().use { reader -> reader.readText() }
        }
        return null
    }
}
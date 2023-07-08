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
import jm.droid.lib.mock.server.corpus.DefaultCorpusImpl
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
class DefaultMockServer(context: Context) : AbsMockServer(context) {

    private val mockFactory by lazy {
        DefaultMockResponseFactory()
    }

    private val iCorpus by lazy {
        DefaultCorpusImpl()
    }

    override fun mockResponse(path: String, param: Buffer): MockResponse? {
        return mockFactory.create(path)?.generateResponse(param, iCorpus)?.let {
            MockResponse().setResponseCode(200)
                .addHeader("Content-Type", it.type)
                .addHeader("Cache-Control", "no-cache")
                .setBody(it.buffer)
        }
    }

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

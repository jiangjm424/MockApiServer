/*
 * Copyright 2023 The Jmdroid Project
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

import com.google.gson.Gson
import jm.droid.lib.mock.server.corpus.ICorpus
import okio.Buffer

interface IMockResponse {
    fun generateResponse(params: Buffer, iCorpus: ICorpus): MockResponseWrapper?
    interface Factory {
        fun create(path: String): IMockResponse?
    }
}

private val gson = Gson()
fun <T> T.toBuffer(): Buffer {
    return gson.toJson(this).toBuffer()
}

fun String.toBuffer(): Buffer {
    return Buffer().use {
        it.writeUtf8(this)
    }
}

fun ByteArray.toBuffer() = Buffer().use {
    it.write(this)
}

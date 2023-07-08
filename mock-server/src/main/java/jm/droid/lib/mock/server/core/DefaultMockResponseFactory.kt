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

import java.lang.Exception
import java.util.Locale

class DefaultMockResponseFactory : IMockResponse.Factory {
    override fun create(path: String): IMockResponse? {
        return try {
            Class.forName(path2Clazz(path)).getConstructor().newInstance() as? IMockResponse
        } catch (e: Exception) {
            null
        }
    }

    private fun path2Clazz(path: String): String {
        val seg = path.split("/").filter { it.isNotBlank() }
        val size = seg.size
        val sb = StringBuilder()
        var i = 0
        val N = size - 1
        while (i < N) {
            sb.append(seg[i].lowercase(Locale.ROOT)).append(".")
            i++
        }
        val clazz = seg.last()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        sb.append(clazz)
        return sb.toString()
    }
}

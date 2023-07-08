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

package com.jm

import jm.droid.lib.mock.server.core.IMockResponse
import jm.droid.lib.mock.server.core.MockResponseWrapper
import jm.droid.lib.mock.server.core.toBuffer
import jm.droid.lib.mock.server.corpus.ICorpus
import jm.droid.sample.mockserver.dto.MockResp
import jm.droid.sample.mockserver.dto.TestData
import okio.Buffer

class Test2 : IMockResponse {
    override fun generateResponse(params: Buffer, iCorpus: ICorpus): MockResponseWrapper? {
        val res = MockResp(ret = 1, errMsg = "ok", data = TestData(icon = iCorpus.icon(), nick = iCorpus.nickName(), des = iCorpus.describe()))
        return MockResponseWrapper(buffer = res.toBuffer())
    }
}

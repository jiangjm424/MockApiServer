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

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import jm.droid.lib.mock.server.corpus.CorpusSets

class MockWebServer : Service() {

    companion object {
        private const val TAG = "MockWebServer"
        const val KEY_EXT_CORPUS = "mock.corpus"
    }
    private lateinit var appMockWebServer: AbsMockServer

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "onCreate .......")
        CorpusSets.Instance.discoverCorpus(this)
        appMockWebServer = DefaultMockServer(this)
        appMockWebServer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy .......")
        appMockWebServer.stop()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

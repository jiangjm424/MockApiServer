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

package jm.droid.lib.mock.server.corpus

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.util.Log
import jm.droid.lib.mock.server.MockInitProvider

class CorpusSets private constructor() : ICorpusSets {

    private val defaultCorpusSets: DefaultCorpusSets = DefaultCorpusSets()
    private var extCorpusSets: ICorpusSets? = null

    fun discoverCorpus(context: Context?) {
        if (context == null) return
        val provider = ComponentName(
            context.packageName,
            MockInitProvider::class.java.name,
        )
        val providerInfo: ProviderInfo =
            context.packageManager.getProviderInfo(provider, PackageManager.GET_META_DATA)
        providerInfo.metaData?.let {
            val extCorpus: String? = it.getString("mock.corpus", null)
            if (extCorpus != null) {
                try {
                    val clazz = Class.forName(extCorpus)
                    extCorpusSets = clazz.getConstructor().newInstance() as? ICorpusSets
                } catch (ex: Exception) {
                    Log.i("CorpusSets", "error init ext corpus")
                }
            }
        }
    }

    override fun nickNames(): List<String> {
        return extCorpusSets?.nickNames() ?: defaultCorpusSets.nickNames()
    }

    override fun icons(): List<String> {
        return extCorpusSets?.icons() ?: defaultCorpusSets.icons()
    }

    override fun images(): List<String> {
        return extCorpusSets?.images() ?: defaultCorpusSets.images()
    }

    override fun titles(): List<String> {
        return extCorpusSets?.titles() ?: defaultCorpusSets.titles()
    }

    override fun tags(): List<String> {
        return extCorpusSets?.tags() ?: defaultCorpusSets.tags()
    }

    override fun codes(): List<Int> {
        return extCorpusSets?.codes() ?: defaultCorpusSets.codes()
    }

    override fun describes(): List<String> {
        return extCorpusSets?.describes() ?: defaultCorpusSets.describes()
    }

    companion object {
        @Volatile
        private var sInstance: CorpusSets? = null

        @JvmStatic
        val Instance: CorpusSets
            get() {
                if (sInstance == null) {
                    synchronized(this::class.java) {
                        if (sInstance == null) {
                            sInstance = CorpusSets()
                        }
                    }
                }
                return sInstance!!
            }
    }
}

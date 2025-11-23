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

package jm.droid.sample.mockserver.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jm.droid.sample.mockserver.dto.MockReq
import jm.droid.sample.mockserver.net.DemoApi
import jm.droid.sample.mockserver.retrofit.NetApiHelper
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val api by lazy {
        NetApiHelper().cc(DemoApi::class.java)
    }
    fun test() {
        viewModelScope.launch {
            try {
                Log.i("jiang", "api.test")
                val bb = api.test2(MockReq("id-mock-req"))
                Log.i("jiang", bb.toString())
                _text.value = bb.toString()
            } catch (e: Exception) {
                Log.i("jiang", "bbb")
                _text.value = "${e.message}"
                e.printStackTrace()
            }
        }
    }
}

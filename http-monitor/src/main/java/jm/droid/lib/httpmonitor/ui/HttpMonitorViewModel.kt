package jm.droid.lib.httpmonitor.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jm.droid.lib.httpmonitor.core.HttpDataTool
import jm.droid.lib.httpmonitor.db.HttpSimpleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HttpMonitorViewModel : ViewModel() {
    private val _httpDatas = MutableLiveData<List<HttpSimpleData>>(emptyList())
    val httpDatas: LiveData<List<HttpSimpleData>> = _httpDatas

    init {
        loadHttpData()
    }

    fun loadHttpData() = viewModelScope.launch(Dispatchers.IO) {
        val list = HttpDataTool.httpDataDao.getSimpleHttpDataList()
        _httpDatas.postValue(list)
    }

    fun clearAllHttpData() = viewModelScope.launch(Dispatchers.IO) {
        HttpDataTool.httpDataDao.clear()
        _httpDatas.postValue(emptyList())
    }
}

package jm.droid.lib.httpmonitor.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import jm.droid.lib.httpmonitor.core.HttpDataTool
import jm.droid.lib.httpmonitor.db.HttpData
import jm.droid.lib.httpmonitor.ui.model.TitleDescDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HttpDetailViewModel : ViewModel() {
    private val _httpData = MutableLiveData<HttpData>()
    private val _httpId = MutableLiveData<Long>()
    val httpId: LiveData<Long> = _httpId

    val httpTitleDescList: LiveData<List<TitleDescDataModel>> = _httpData.map {
        val ll = mutableListOf<TitleDescDataModel>()
        ll.add(TitleDescDataModel("method", it.method))
        ll.add(TitleDescDataModel("scheme", it.scheme))
        ll.add(TitleDescDataModel("host", it.host))
        ll.add(TitleDescDataModel("path", it.path))
        ll.add(TitleDescDataModel("protocol", it.protocol))
        ll.add(TitleDescDataModel("url", it.url))
        ll.add(TitleDescDataModel("requestTime", timestampToHuman(it.requestTime)))
        ll.add(TitleDescDataModel("requestBody", it.requestBodyDesc))
        ll.add(TitleDescDataModel("requestContentLength", longToHuman(it.requestContentLength, "bytes")))
        ll.add(TitleDescDataModel("requestContentType", it.requestContentType))
        ll.add(TitleDescDataModel("requestHeaders", it.requestHeaders))
        ll.add(TitleDescDataModel("responseCode", "${it.responseCode}"))
        ll.add(TitleDescDataModel("responseTime", timestampToHuman(it.responseTime)))
        ll.add(TitleDescDataModel("responseBody", it.responseBodyDesc))
        ll.add(TitleDescDataModel("responseContentLength", longToHuman(it.responseContentLength, "bytes")))
        ll.add(TitleDescDataModel("responseContentType", it.responseContentType))
        ll.add(TitleDescDataModel("responseHeaders", it.responseHeaders))
        ll.add(TitleDescDataModel("duration", longToHuman(it.duration, "ms")))
        ll.add(TitleDescDataModel("errorMsg", it.errorMsg))
        ll.add(TitleDescDataModel("exception", it.exception))
        ll
    }

    fun loadHttpData(httpId: Long) = viewModelScope.launch(Dispatchers.IO) {
        if (httpId <= 0) return@launch
        _httpId.postValue(httpId)
        val data = HttpDataTool.httpDataDao.getHttpData(httpId)
        _httpData.postValue(data)
    }

    private fun timestampToHuman(t: Long?): String {
        if (t == null) return "N/A"
        return formatTime(t)
    }

    private fun longToHuman(l: Long?, unit: String): String {
        if (l == null) return "N/A"
        return "$l $unit"
    }
}

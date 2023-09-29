package jm.droid.lib.httpmonitor.ui

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import jm.droid.lib.httpmonitor.databinding.HttpSimpleDataCardLayoutBinding
import jm.droid.lib.httpmonitor.db.HttpSimpleData
import jm.droid.lib.samatadapter.core.ViewBindingDelegate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.TimeUnit

class HttpSimpleDataCard :
    ViewBindingDelegate<HttpSimpleData, HttpSimpleDataCardLayoutBinding>() {
    override fun onCreateViewBinding(
        context: Context,
        parent: ViewGroup
    ): HttpSimpleDataCardLayoutBinding {
        return HttpSimpleDataCardLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    /**
     * 创建的时候对view进行一些初始化操作，比如点击事件，recycle view的初始化等操作
     */
    override fun onConfigViewHolder(holder: Holder<HttpSimpleDataCardLayoutBinding>) {
//        holder.itemView.setOnClickListener {
//            val item = holder.data as? HttpSimpleDataDataModel
//        }
    }

    override fun onBindItem(
        view: HttpSimpleDataCardLayoutBinding,
        pos: Int,
        item: HttpSimpleData
    ) {
        view.method.text = item.method
        view.host.text = item.host
        view.responseCode.text = item.responseCode.toString()
        view.path.text = item.path
        view.duration.text = "${item.duration} ms"
        view.requestTime.text = formatTime(item.requestTime ?: 0)
        view.method.text = item.method
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(date)
        return format.format(date)

    }
}

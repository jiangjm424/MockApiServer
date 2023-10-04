package jm.droid.lib.httpmonitor.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import jm.droid.lib.httpmonitor.databinding.HttpSimpleDataCardLayoutBinding
import jm.droid.lib.httpmonitor.db.HttpSimpleData
import jm.droid.lib.samatadapter.core.ViewBindingDelegate

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
        holder.itemView.setOnClickListener {
            val item = holder.data as? HttpSimpleData ?: return@setOnClickListener
            HttpDetailActivity.start(holder.itemView.context, item.id)
        }
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

}

package jm.droid.lib.httpmonitor.ui

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import jm.droid.lib.httpmonitor.databinding.TitleDescCardLayoutBinding
import jm.droid.lib.httpmonitor.ui.model.TitleDescDataModel
import jm.droid.lib.samatadapter.core.ViewBindingDelegate

class TitleDescCard : ViewBindingDelegate<TitleDescDataModel, TitleDescCardLayoutBinding>() {
    override fun onCreateViewBinding(
        context: Context,
        parent: ViewGroup
    ): TitleDescCardLayoutBinding {
        return TitleDescCardLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    /**
     * 创建的时候对view进行一些初始化操作，比如点击事件，recycle view的初始化等操作
     */
    override fun onConfigViewHolder(holder: Holder<TitleDescCardLayoutBinding>) {
//        holder.itemView.setOnClickListener {
//            val item = holder.data as? TitleDescDataModel
//        }
    }

    override fun onBindItem(view: TitleDescCardLayoutBinding, pos: Int, item: TitleDescDataModel) {
        view.title.text = item.title
        view.detail.text = item.detail.toDesc()
    }

    private fun String?.toDesc(): String {
        if (isNullOrEmpty()) return "N/A"
        return this
    }

}

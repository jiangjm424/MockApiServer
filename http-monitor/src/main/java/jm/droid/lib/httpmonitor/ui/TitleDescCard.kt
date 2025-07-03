package jm.droid.lib.httpmonitor.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
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
        holder.binding.detail.setOnLongClickListener {
            clip2board(holder.itemView.context, holder.binding.detail.text)
            true
        }
    }

    override fun onBindItem(view: TitleDescCardLayoutBinding, pos: Int, item: TitleDescDataModel) {
        view.title.text = item.title
        view.detail.text = item.detail.toDesc()
    }

    private fun String?.toDesc(): String {
        if (isNullOrEmpty()) return "N/A"
        return this
    }

    private fun clip2board(context: Context, desc: CharSequence) {
        val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clip?.setPrimaryClip(ClipData.newPlainText(null, desc))
        Toast.makeText(context, "内容已经复制到粘贴板中！！", Toast.LENGTH_LONG).show()
    }
}

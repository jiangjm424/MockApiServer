package jm.droid.lib.httpmonitor.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import jm.droid.lib.httpmonitor.R
import jm.droid.lib.httpmonitor.databinding.ActivityHttpDetailBinding
import jm.droid.lib.samatadapter.ISmartAdapter

class HttpDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHttpDetailBinding
    private val viewModel by viewModels<HttpDetailViewModel>()
    private val adapter by lazy {
        ISmartAdapter.Builder.with(this).register(TitleDescCard()).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHttpDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.httpTitleDescList.observe(this) {
            adapter.setData(it)
        }
        binding.httpDataDetailList.adapter = adapter.adapter
        binding.httpDataDetailList.layoutManager = LinearLayoutManager(this)
        val id = savedInstanceState?.getLong(HTTP_ID) ?: intent.getLongExtra(HTTP_ID, 0)
        binding.appTitle.text = String.format(getString(R.string.app_http_detail_title, id))
        viewModel.loadHttpData(id)
    }

    companion object {
        private const val HTTP_ID = "http_id"
        fun start(context: Context, httpId: Long) {
            val intent = Intent(context, HttpDetailActivity::class.java)
            intent.putExtra(HTTP_ID, httpId)
            context.startActivity(intent)
        }
    }
}

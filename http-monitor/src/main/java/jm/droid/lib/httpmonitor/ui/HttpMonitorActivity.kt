package jm.droid.lib.httpmonitor.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import jm.droid.lib.httpmonitor.databinding.ActivityHttpMonitorBinding
import jm.droid.lib.samatadapter.ISmartAdapter

class HttpMonitorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHttpMonitorBinding
    private val viewModel by viewModels<HttpMonitorViewModel>()
    private val adapter by lazy {
        ISmartAdapter.Builder.with(this)
            .register(HttpSimpleDataCard())
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHttpMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.httpDataList.adapter = adapter.adapter
        binding.httpDataList.layoutManager = LinearLayoutManager(this)
        viewModel.httpDatas.observe(this) {
            adapter.setData(it)
        }
        viewModel.loadHttpData()
    }
}

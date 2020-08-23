package com.jk.apisdemo.history

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.jk.apisdemo.R
import com.jk.apisdemo.event.OnAPIsUpdate
import com.jk.apisdemo.impl.FetchApiHelper
import com.jk.apisdemo.impl.LogDbHelper
import com.jk.apisdemo.model.ApiLog
import com.jk.apisdemo.service.ApiService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HistoryActivity : AppCompatActivity() {
    private var apiHelper: FetchApiHelper? = null
    private var recyclerView: RecyclerView? = null
    private var historyAdapter: HistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setTitle(R.string.history)
        apiHelper = FetchApiHelper(this)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        historyAdapter = HistoryAdapter(this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = layoutManager
        recyclerView!!.addItemDecoration(decoration)
        recyclerView?.adapter = historyAdapter

        val cb = object : FetchApiHelper.OnFetchLogsCallback {
            override fun OnFetchLogs(logs: ArrayList<ApiLog>) {
                historyAdapter?.setData(logs)
            }
        }
        apiHelper?.fetchAllLogs(cb)
    }

    override fun onDestroy() {
        super.onDestroy()
        apiHelper?.onDestroy()
    }

}
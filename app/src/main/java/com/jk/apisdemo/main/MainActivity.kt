package com.jk.apisdemo.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jk.apisdemo.R
import com.jk.apisdemo.event.OnAPIsUpdate
import com.jk.apisdemo.impl.FetchApiHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {
    private var apiHelper: FetchApiHelper? = null
    private var recyclerView: RecyclerView? = null
    private var apisAdapter: ApisAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        initView()
    }

    private fun initView() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setTitle(R.string.title)
        setupRecyclerView()
        apiHelper = FetchApiHelper(this)
        apiHelper?.restoreLastLog()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        apisAdapter = ApisAdapter()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = layoutManager
        recyclerView!!.addItemDecoration(decoration)
        recyclerView?.adapter = apisAdapter
    }

    override fun onResume() {
        super.onResume()
        apiHelper?.startFetching()
    }

    override fun onPause() {
        super.onPause()
        apiHelper?.stopFetching()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        apiHelper?.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OnAPIsUpdate?) {
        apisAdapter?.setData(event?.apis)
    }

}
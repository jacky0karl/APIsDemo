package com.jk.apisdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jk.apisdemo.impl.FetchApiHelper


class MainActivity : AppCompatActivity() {
    private var apiHelper: FetchApiHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setTitle(R.string.title)
        apiHelper = FetchApiHelper()
    }

    override fun onResume() {
        super.onResume()
        apiHelper?.startFetching()
    }

    override fun onPause() {
        super.onPause()
        apiHelper?.stopFetching()
    }

}
package com.jk.apisdemo.impl

import android.util.Log
import com.jk.apisdemo.service.ApiService

class FetchApiHelper {
    companion object {
        private const val TAG = "FetchApiHelper"
    }

    fun startFetching() {
        val cb = object : ApiService.OnAPIsCallback {
            override fun onFetchSucc(response: String) {
                Log.d(TAG, response)
            }
        }

        ApiService.getInstance().fetchApis(cb)
    }

    fun stopFetching() {
        ApiService.getInstance().cancelAll()
    }

}
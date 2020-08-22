package com.jk.apisdemo.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jk.apisdemo.App
import com.jk.apisdemo.R
import com.jk.apisdemo.extended.toast

class ApiService private constructor() {
    interface OnAPIsCallback {
        fun onFetchSucc(response: String)
    }

    companion object {
        private const val TAG = "ApiService"
        private const val URL = "https://api.github.com"
        private var instance: ApiService? = null

        @Synchronized
        fun getInstance(): ApiService {
            if (instance == null) {
                instance = ApiService()
            }
            return instance!!
        }
    }

    private var queue: RequestQueue? = null

    init {
        queue = Volley.newRequestQueue(App.context)
    }

    fun fetchApis(cb: OnAPIsCallback) {
        if (!isNetworkAvailable()) {
            App.context.toast(R.string.no_network)
            return
        }

        App.context.toast(R.string.fetching)
        val stringRequest = StringRequest(
            Request.Method.GET, Companion.URL,
            Response.Listener<String> { response ->
                cb.onFetchSucc(response)
            },
            Response.ErrorListener { error ->
                Log.d(TAG, error.message)
            })

        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun cancelAll() {
        queue?.cancelAll(TAG)
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = App.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

}
package com.jk.apisdemo.impl

import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jk.apisdemo.event.OnAPIsUpdate
import com.jk.apisdemo.model.Api
import com.jk.apisdemo.service.ApiService
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class FetchApiHelper {
    companion object {
        private const val TAG = "FetchApiHelper"
        private const val INTERVAL = 5000L
    }

    var activity: Activity? = null
    var timer: Timer? = null
    var task: TimerTask? = null

    constructor(activity: Activity) {
        this.activity = activity
    }

    fun startFetching() {
        timer = Timer(true)
        task = object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread(Runnable {
                    doFetching()
                })
            }
        }

        timer?.schedule(task, INTERVAL, INTERVAL)
    }

    fun stopFetching() {
        timer?.cancel()
        ApiService.getInstance().cancelAll()
    }

    fun doFetching() {
        val cb = object : ApiService.OnAPIsCallback {
            override fun onFetchSucc(response: String) {
                Log.d(TAG, response)
                val list = parseResponse(response)
                EventBus.getDefault().post(OnAPIsUpdate(list))
            }
        }

        Log.e(TAG, "doFetching")
        ApiService.getInstance().fetchApis(cb)
    }

    fun parseResponse(response: String): ArrayList<Api> {
        val type: Type = object : TypeToken<HashMap<String?, String?>?>() {}.type
        val map: HashMap<String, String> = Gson().fromJson(response, type)
        val list: ArrayList<Api> = ArrayList<Api>()
        for ((key, value) in map) {
            val api = Api(key, value)
            list.add(api)
        }
        return list
    }

}
package com.jk.apisdemo.impl

import android.app.Activity
import android.content.ContentValues
import android.util.Log
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.jk.apisdemo.event.OnAPIsUpdate
import com.jk.apisdemo.model.Api
import com.jk.apisdemo.model.ApiLog
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
    var dbHelper: LogDbHelper? = null


    constructor(activity: Activity) {
        this.activity = activity
        dbHelper = LogDbHelper(activity)
    }

    fun onDestroy() {
        dbHelper?.close()
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
                insertLog(LogDbHelper.STATUS_OK, response)
            }

            override fun onFetchFail(error: VolleyError) {
                insertLog(LogDbHelper.STATUS_NOK, error.localizedMessage ?: "")
            }
        }

        Log.e(TAG, "doFetching")
        ApiService.getInstance().fetchApis(cb)
    }

    fun restoreLastLog() {
        val logs = queryLog(true)
        if (logs.isNotEmpty()) {
            val list = logs.get(0).response?.let { parseResponse(it) }
            EventBus.getDefault().post(list?.let { OnAPIsUpdate(it) })
        }
    }

    fun insertLog(status: Int, response: String) {
        val time = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(LogDbHelper.COLUMN_NAME_STATUS, status)
            put(LogDbHelper.COLUMN_NAME_RESPONSE, response)
            put(LogDbHelper.COLUMN_NAME_DATE, time)
        }

        val db = dbHelper!!.writableDatabase
        db?.insert(LogDbHelper.TABLE_NAME, null, values)
        db.close()
    }

    fun queryLog(onlyLast: Boolean): ArrayList<ApiLog> {
        val projection = arrayOf(
            LogDbHelper.COLUMN_NAME_STATUS,
            LogDbHelper.COLUMN_NAME_RESPONSE, LogDbHelper.COLUMN_NAME_DATE
        )
        val sortOrder = "${LogDbHelper.COLUMN_NAME_DATE} DESC"
        val limit = if (onlyLast) "1" else null

        val db = dbHelper!!.readableDatabase
        val cursor = db.query(
            LogDbHelper.TABLE_NAME, projection, null,
            null, null, null, sortOrder, limit
        )

        val list = ArrayList<ApiLog>()
        with(cursor) {
            while (moveToNext()) {
                val status = getInt(getColumnIndexOrThrow(LogDbHelper.COLUMN_NAME_STATUS))
                val response = getString(getColumnIndexOrThrow(LogDbHelper.COLUMN_NAME_RESPONSE))
                val date = getLong(getColumnIndexOrThrow(LogDbHelper.COLUMN_NAME_DATE))
                val log = ApiLog(status, response, date)
                list.add(log)
            }
            close()
        }

        db.close()
        return list
    }


    fun parseResponse(response: String): ArrayList<Api> {
        val list: ArrayList<Api> = ArrayList<Api>()
        try {
            val type: Type = object : TypeToken<HashMap<String?, String?>?>() {}.type
            val map: HashMap<String, String> = Gson().fromJson(response, type)

            for ((key, value) in map) {
                val api = Api(key, value)
                list.add(api)
            }
        } catch (e: JsonSyntaxException) {
        }
        return list
    }

}
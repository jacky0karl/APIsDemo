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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class FetchApiHelper {
    companion object {
        private const val TAG = "FetchApiHelper"
        private const val INTERVAL = 5000L
    }

    interface OnFetchLogsCallback {
        fun OnFetchLogs(logs: ArrayList<ApiLog>)
    }

    var activity: Activity? = null
    var timer: Timer? = null
    var task: TimerTask? = null
    var dbHelper: LogDbHelper? = null
    var disposable: CompositeDisposable? = null

    constructor(activity: Activity) {
        this.activity = activity
        dbHelper = LogDbHelper(activity)
        disposable = CompositeDisposable()
    }

    fun onDestroy() {
        dbHelper?.close()
        disposable?.dispose()
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
        val observable = Observable.create<ArrayList<ApiLog>> { emitter ->
            val logs = queryLogSync(true)
            emitter.onNext(logs)
        }

        val consumer = Consumer<ArrayList<ApiLog>> { logs ->
            if (logs.isNotEmpty()) {
                val list = logs.get(0).response?.let { parseResponse(it) }
                EventBus.getDefault().post(list?.let { OnAPIsUpdate(it) })
            }
        }

        val disp = observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)
        disposable?.add(disp)
    }

    fun fetchAllLogs(cb: OnFetchLogsCallback) {
        val observable = Observable.create<ArrayList<ApiLog>> { emitter ->
            val logs = queryLogSync(false)
            emitter.onNext(logs)
        }

        val consumer = Consumer<ArrayList<ApiLog>> { logs ->
            if (logs.isNotEmpty()) {
                cb.OnFetchLogs(logs)
            }
        }

        val disp = observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)
        disposable?.add(disp)
    }

    fun insertLog(status: Int, response: String) {
        val observable = Observable.create<Long> { emitter ->
            val rowId = insertLogSync(status, response)
            emitter.onNext(rowId)
        }

        val consumer = Consumer<Long> { _ -> }

        val disp = observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)
        disposable?.add(disp)
    }

    fun insertLogSync(status: Int, response: String): Long {
        val time = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(LogDbHelper.COLUMN_NAME_STATUS, status)
            put(LogDbHelper.COLUMN_NAME_RESPONSE, response)
            put(LogDbHelper.COLUMN_NAME_DATE, time)
        }

        val db = dbHelper!!.writableDatabase
        val rowId = db?.insert(LogDbHelper.TABLE_NAME, null, values)
        db.close()
        return rowId ?: -1
    }

    fun queryLogSync(onlyLast: Boolean): ArrayList<ApiLog> {
        val projection = arrayOf(
            LogDbHelper.COLUMN_NAME_STATUS,
            LogDbHelper.COLUMN_NAME_RESPONSE, LogDbHelper.COLUMN_NAME_DATE
        )
        val selection = if (onlyLast) "${LogDbHelper.COLUMN_NAME_STATUS} = ?" else null
        val selectionArgs = if (onlyLast) arrayOf("${LogDbHelper.STATUS_OK}") else null
        val sortOrder = "${LogDbHelper.COLUMN_NAME_DATE} DESC"
        val limit = if (onlyLast) "1" else "100"

        val db = dbHelper!!.readableDatabase
        val cursor = db.query(
            LogDbHelper.TABLE_NAME, projection, selection,
            selectionArgs, null, null, sortOrder, limit
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
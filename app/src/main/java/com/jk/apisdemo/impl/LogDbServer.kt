package com.jk.apisdemo.impl

import android.content.ContentValues
import android.content.Context
import com.jk.apisdemo.App
import com.jk.apisdemo.model.ApiLog

class LogDbServer(context: Context) {

    private var dbHelper: LogDbHelper? = null

    init {
        dbHelper = LogDbHelper(context)
    }

    fun onDestroy() {
        dbHelper?.close()
    }

    fun insert(status: Int, response: String): Long {
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

    fun query(onlyLast: Boolean): ArrayList<ApiLog> {
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

}





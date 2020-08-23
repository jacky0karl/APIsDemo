package com.jk.apisdemo.impl

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns


class LogDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ApiLog.db"
        const val TABLE_NAME = "ApiLog"
        const val COLUMN_NAME_STATUS = "status"
        const val COLUMN_NAME_RESPONSE = "response"
        const val COLUMN_NAME_DATE = "date"
        const val STATUS_OK = 200
        const val STATUS_NOK = -1

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${COLUMN_NAME_STATUS} INTEGER," +
                    "${COLUMN_NAME_RESPONSE} TEXT," +
                    "${COLUMN_NAME_DATE} LONG)"
    }

}

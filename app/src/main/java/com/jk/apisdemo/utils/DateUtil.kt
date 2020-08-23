package com.jk.apisdemo.utils

import java.text.DateFormat
import java.util.*

class DateUtil {
    companion object {
        fun getDateTime(milliSec: Long): String {
            val date = Date(milliSec)
            return DateFormat.getDateTimeInstance().format(date)
        }
    }
}
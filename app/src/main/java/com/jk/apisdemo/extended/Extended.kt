package com.jk.apisdemo.extended

import android.content.Context
import android.widget.Toast


fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, length).show()

fun Context.toast(message: Int, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, length).show()
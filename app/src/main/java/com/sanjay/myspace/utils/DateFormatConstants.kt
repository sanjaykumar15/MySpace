package com.sanjay.myspace.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
object DateFormatConstants {
    val uiDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a")
    val fileDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
}
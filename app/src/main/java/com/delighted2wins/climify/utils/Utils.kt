package com.delighted2wins.climify.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun timeStampToHumanDate(timeStamp: Long, format: String): String {

    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(timeStamp * 1000)
}
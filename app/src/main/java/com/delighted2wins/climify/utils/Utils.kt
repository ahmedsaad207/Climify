package com.delighted2wins.climify.utils

import com.delighted2wins.climify.R
import java.text.SimpleDateFormat
import java.util.Locale

fun timeStampToHumanDate(timeStamp: Long, format: String): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(timeStamp * 1000)
}

fun getCountryNameFromCode(countryCode: String): String? {
    val locale = Locale("", countryCode)
    return locale.displayCountry
}

fun getDrawableFromIconCode(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable._1d
        "02d" -> R.drawable._02d
        "03d" -> R.drawable._3d
        "04d" -> R.drawable._04d
        "09d" -> R.drawable._09d
        "10d" -> R.drawable._10d
        "11d" -> R.drawable._11d
        "13d" -> R.drawable._13d
        "50d" -> R.drawable._50d
        "01n" -> R.drawable._1n
        "02nd" -> R.drawable._02n
        "03n" -> R.drawable._3d
        "04n" -> R.drawable._04d
        "09n" -> R.drawable._09n
        "10n" -> R.drawable._10n
        "11n" -> R.drawable._11n
        "13n" -> R.drawable._13night
        "50n" -> R.drawable._50d
        else -> R.drawable._3d
    }
}

fun getBackgroundDrawableFromIconCode(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable.clear_sky
        "02d" -> R.drawable.clouds
        "03d" -> R.drawable.clouds
        "04d" -> R.drawable.clouds
        "09d" -> R.drawable.rain_d
        "10d" -> R.drawable.rain_d
        "11d" -> R.drawable.thunder
        "13d" -> R.drawable.snow
        "50d" -> R.drawable.mist
        "01n" -> R.drawable.clear_sky
        "02nd" -> R.drawable.clouds
        "03n" -> R.drawable.clouds
        "04n" -> R.drawable.clouds
        "09n" -> R.drawable.rain
        "10n" -> R.drawable.rain
        "11n" -> R.drawable.thunder
        "13n" -> R.drawable.snow
        "50n" -> R.drawable.mist
        else -> R.drawable.clear_sky
    }
}
package com.delighted2wins.climify.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import com.delighted2wins.climify.R
import com.delighted2wins.climify.domainmodel.ForecastWeather
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.enums.WindSpeedUnit
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun timeStampToHumanDate(timeStamp: Long, format: String): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(timeStamp * 1000)
}

fun Long.toFormat(format: String): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(this)
}

fun String.getCountryNameFromCode(): String? {
    val locale = Locale("", this)
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

fun filterForecastToHoursAndDays(
    currentWeather: ForecastWeather, forecastList: List<ForecastWeather>
): Pair<List<ForecastWeather>, List<ForecastWeather>> {
    val hours = mutableListOf<ForecastWeather>()
    val days = mutableListOf<ForecastWeather>()
    hours.add(currentWeather)

    val calendar = Calendar.getInstance()
    val currentDate = calendar.time
    val formattedCurrentDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(currentDate)

    forecastList.forEach { weather ->
        val dateAndTime = weather.dateText.split(" ")
        if (dateAndTime.contains(formattedCurrentDate)) {
            hours.add(weather)
        } else { // days
            if (weather.dateText.substring(11, 16) == "12:00") {
                days.add(weather)
            }
        }
    }
    return Pair(hours, days)
}

fun Context.updateAppLanguage(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}

fun Context.restartActivity() {
    val intent = (this as Activity).intent
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
    (this as? Activity)?.finish()
}

private fun convertToArabicNumbers(number: String): String {
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
}

fun Int.toLocalizedNumber(): String {
    val language = Locale.getDefault().language
    return if (language == "ar") convertToArabicNumbers(this.toString()) else this.toString()
}

fun Context.getTempUnitSymbol(unit: String): String {
    return when (unit) {
        "standard" -> getString(R.string.kelvin_symbol)
        "metric" -> getString(R.string.celsius_symbol)
        "imperial" -> getString(R.string.fahrenheit_symbol)
        else -> getString(R.string.celsius_symbol)
    }
}

fun Context.getWindSpeedUnitSymbol(unit: String): String {
    return when (unit) {
        "standard" -> getString(R.string.meter_sec)
        "metric" -> getString(R.string.meter_sec)
        "imperial" -> getString(R.string.mile_hour)
        else -> getString(R.string.meter_sec)
    }
}

@SuppressLint("MissingPermission")
fun Context.getUserLocationUsingGps(onResult: (latitude: Double, longitude: Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onResult(location.latitude, location.longitude)
        } else {
            onResult(0.0, 0.0)
        }
    }.addOnFailureListener {
        onResult(0.0, 0.0)
    }
}

fun String.convertTemp(value: Double, appUnit: String): Double {
    return when (appUnit) {
        TempUnit.STANDARD.value -> when (this) {  // Convert to Kelvin
            TempUnit.METRIC.value -> value + 273.15  // Celsius to Kelvin
            TempUnit.IMPERIAL.value -> (value - 32) * 5.0 / 9.0 + 273.15  // Fahrenheit to Kelvin
            else -> value  // Already in Kelvin
        }

        TempUnit.METRIC.value -> when (this) {  // Convert to Celsius
            TempUnit.STANDARD.value -> value - 273.15  // Kelvin to Celsius
            TempUnit.IMPERIAL.value -> (value - 32) * 5.0 / 9.0  // Fahrenheit to Celsius
            else -> value  // Already in Celsius
        }

        TempUnit.IMPERIAL.value -> when (this) {  // Convert to Fahrenheit
            TempUnit.STANDARD.value -> (value - 273.15) * 9.0 / 5.0 + 32  // Kelvin to Fahrenheit
            TempUnit.METRIC.value -> (value * 9.0 / 5.0) + 32  // Celsius to Fahrenheit
            else -> value  // Already in Fahrenheit
        }

        else -> value
    }
}

fun String.convertWindSpeed(value: Double, appUnit: String): Double {
    return when (appUnit) {
        WindSpeedUnit.STANDARD.value, WindSpeedUnit.METRIC.value -> when (this) {
            WindSpeedUnit.IMPERIAL.value -> value * 0.44704  // mph to m/s
            else -> value  // Already in m/s (standard & metric are the same)
        }

        WindSpeedUnit.IMPERIAL.value -> when (this) {
            WindSpeedUnit.STANDARD.value, WindSpeedUnit.METRIC.value -> value * 2.23694  // m/s to mph
            else -> value  // Already in mph
        }

        else -> value  // Default case (if appUnit is invalid)
    }
}



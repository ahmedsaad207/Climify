package com.delighted2wins.climify.utils

import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.ForecastWeather

fun CurrentWeatherResponse.toCurrentWeather(): CurrentWeather {
    return CurrentWeather(
        city = name ?: "",
        dt = dt ?: 0,
        dateText = timeStampToHumanDate(dt?.toLong() ?: 0L, "EEE, dd MMM"),
        timeText = timeStampToHumanDate(dt?.toLong() ?: 0L, "h:mm a"),
        icon = getDrawableFromIconCode(weather[0].icon ?: "0"),
        temp = main?.temp?.toInt()?.toLocalizedNumber() ?: "0",
        tempMin = main?.tempMin ?: 0.0,
        tempMax = main?.tempMax ?: 0.0,
        pressure = if (main?.pressure != null) "${main?.pressure?.toLocalizedNumber()} " else "0",
        humidity = if (main?.humidity != null) "${main?.humidity?.toLocalizedNumber()} " else "0",
        windSpeed = wind?.speed ?: 0.0,
        cloud = if (clouds?.all != null) "${clouds?.all?.toLocalizedNumber()} " else "0",
        description = weather[0].description ?: "",
        lat = coord?.lat ?: 0.0,
        long = coord?.lon ?: 0.0,
        background = getBackgroundDrawableFromIconCode(weather[0].icon ?: "0"),
        country = sys?.country ?: "",
        id = id ?: -1
    )
}

fun UpcomingForecastResponse.toForecastWeatherList(unit: String): List<ForecastWeather> {
    val forecastWeathers = arrayListOf<ForecastWeather>()
    forecastWeatherList.forEach {
        forecastWeathers.add(
            ForecastWeather(
                date = it.dt ?: 0,
                time = timeStampToHumanDate(it.dt?.toLong() ?: 0L, "h:mm a"),
                icon = getDrawableFromIconCode(it.weathers[0].icon ?: "0"),
                temp = it.main?.temp?.toInt()?.toLocalizedNumber() ?: "0",
                tempMin = it.main?.tempMin ?: 0.0,
                tempMax = it.main?.tempMax ?: 0.0,
                description = it.weathers[0].description ?: "",
                dateText = it.dtTxt ?: "",
                unit = unit
            )
        )

    }
    return forecastWeathers
}

fun CurrentWeather.toForecastWeather(): ForecastWeather {
   return ForecastWeather(
        dateText = "",
        time = "Now",
        date = dt,
        icon = icon,
        temp = temp,
        tempMin = tempMin,
        tempMax = tempMax,
        description = description
    )
}
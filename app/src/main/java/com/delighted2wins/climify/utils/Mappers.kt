package com.delighted2wins.climify.utils

import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.model.CurrentWeather
import com.delighted2wins.climify.model.ForecastWeather

fun CurrentWeatherResponse.toCurrentWeather(): CurrentWeather {
    return CurrentWeather(
        city = name ?: "",
        dt = dt ?: 0,
        dateText = timeStampToHumanDate(dt?.toLong() ?: 0L, "EEE, dd MMM"),
        timeText = timeStampToHumanDate(dt?.toLong() ?: 0L, "h:mm a"),
        icon = "https://openweathermap.org/img/wn/${weather[0].icon}@2x.png",
        temp = main?.temp ?: 0.0,
        tempMin = main?.tempMin ?: 0.0,
        tempMax = main?.tempMax ?: 0.0,
        pressure = if (main?.pressure != null) "${main?.pressure} hPa" else "0 hPa",
        humidity = if (main?.humidity != null) "${main?.humidity} %" else "0 %",
        windSpeed = wind?.speed ?: 0.0,
        cloud = if (clouds?.all != null) "${clouds?.all} %" else "0 %",
        description = weather[0].description ?: "",
        lat = coord?.lat ?: 0.0,
        long = coord?.lon ?: 0.0
    )
}

fun UpcomingForecastResponse.toForecastWeatherList(): List<ForecastWeather> {
    val forecastWeathers = arrayListOf<ForecastWeather>()
    forecastWeatherList.forEach {
        forecastWeathers.add(
            ForecastWeather(
                date = it.dt ?: 0,
                time = timeStampToHumanDate(it.dt?.toLong() ?: 0L, "h:mm a"),
                icon = "https://openweathermap.org/img/wn/${it.weathers[0].icon}@2x.png",/*it.weathers[0].icon ?: "",*/
                temp = it.main?.temp ?: 0.0,
                tempMin = it.main?.tempMin ?: 0.0,
                tempMax = it.main?.tempMax ?: 0.0,
                description = it.weathers[0].description ?: "",
                dateText = it.dtTxt ?: "",
            )
        )

    }
    return forecastWeathers
}
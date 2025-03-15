package com.delighted2wins.climify.model

import com.delighted2wins.climify.data.model.Wind

data class CurrentWeather(
    var city: String,
    var dt: Int,
    var dateText: String,
    var timeText: String,
    var icon: String,
    var temp: Double,
    var tempMin: Double,
    val tempMax: Double,
    var pressure: String,
    val humidity: String,
    val windSpeed: Double,
    var cloud: String,
    var description: String
)
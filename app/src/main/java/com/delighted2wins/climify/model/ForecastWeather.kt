package com.delighted2wins.climify.model

data class ForecastWeather(
    var date: Int,
    var time: String,
    var dateText: String,
    var icon: String,
    var temp: Double,
    var tempMin: Double,
    val tempMax: Double,
    var description: String
)
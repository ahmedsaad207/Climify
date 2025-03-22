package com.delighted2wins.climify.domainmodel

data class ForecastWeather(
    var date: Int,
    var time: String,
    var dateText: String,
    var icon: Int,
    var temp: Double,
    var tempMin: Double,
    val tempMax: Double,
    var description: String
)
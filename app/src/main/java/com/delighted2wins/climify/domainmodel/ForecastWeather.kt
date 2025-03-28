package com.delighted2wins.climify.domainmodel

import com.delighted2wins.climify.enums.TempUnit

data class ForecastWeather(
    var date: Int,
    var time: String,
    var dateText: String,
    var icon: Int,
    var temp: String,
    var tempMin: Double,
    val tempMax: Double,
    var description: String,
    var unit: String = TempUnit.METRIC.value
)
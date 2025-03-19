package com.delighted2wins.climify.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrentWeather(
    @PrimaryKey
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
    var lat: Double,
    var long: Double,
    var description: String
)
package com.delighted2wins.climify.domainmodel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrentWeather(
    var city: String,
    @PrimaryKey
    var dt: Int,
    var dateText: String,
    var timeText: String,
    var icon: Int,
    var background: Int,
    var temp: Double,
    var tempMin: Double,
    val tempMax: Double,
    var pressure: String,
    val humidity: String,
    val windSpeed: Double,
    var cloud: String,
    var lat: Double,
    var long: Double,
    var description: String,
    var country: String
)
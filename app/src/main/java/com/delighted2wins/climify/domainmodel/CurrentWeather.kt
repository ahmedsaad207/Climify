package com.delighted2wins.climify.domainmodel

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.delighted2wins.climify.data.local.db.Converters
import com.delighted2wins.climify.enums.TempUnit

@Entity
data class CurrentWeather(
    @PrimaryKey(false)
    var id: Int,
    var city: String,
    var dt: Int,
    var dateText: String,
    var timeText: String,
    var icon: Int,
    var background: Int,
    var temp: String,
    var tempMin: Double,
    val tempMax: Double,
    var pressure: String,
    val humidity: String,
    val windSpeed: Double,
    var cloud: String,
    var lat: Double,
    var long: Double,
    var description: String,
    var country: String,
    var unit: String = TempUnit.METRIC.value,
    @TypeConverters(Converters::class)
    var hoursForecast: List<ForecastWeather>,
    @TypeConverters(Converters::class)
    var daysForecast: List<ForecastWeather>
)
package com.delighted2wins.climify.data.local.db

import androidx.room.TypeConverter
import com.delighted2wins.climify.domainmodel.ForecastWeather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromList(weathers: List<ForecastWeather>): String {
        return gson.toJson(weathers)
    }

    @TypeConverter
    fun toList(json: String): List<ForecastWeather> {
        val forecastList = object : TypeToken<List<ForecastWeather>>() {}.type
        return gson.fromJson(json, forecastList)
    }
}
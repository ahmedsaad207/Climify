package com.delighted2wins.climify.data.repo

import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.domainmodel.Alarm
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.LocationInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<CurrentWeatherResponse>

    suspend fun getUpcomingForecast(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<UpcomingForecastResponse>

    suspend fun getStateInfoByLocation(
        lat: Double,
        lon: Double,
    ): List<LocationInfo>

    suspend fun getLocationByQuery(
        query: String
    ): List<LocationInfo>

    suspend fun insertWeather(weather: CurrentWeather): Long

    suspend fun getFavoriteWeathers(): Flow<List<CurrentWeather>>

    fun getWeatherById(id: Int): Flow<CurrentWeather>

    suspend fun updateWeather(weather: CurrentWeather)

    suspend fun deleteWeather(weather: CurrentWeather)

    fun <T> saveData(value: T)

    fun <T> getData(type: String): T

    suspend fun insertAlarm(alarm: Alarm): Long

    fun getAllAlarms(): Flow<List<Alarm>>

    suspend fun deleteAlarm(alarm: Alarm): Int

    fun getCachedWeather(): Flow<CurrentWeather>
}
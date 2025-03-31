package com.delighted2wins.climify.data.local.db

import com.delighted2wins.climify.domainmodel.CurrentWeather
import kotlinx.coroutines.flow.Flow

interface WeathersLocalDataSource {
    suspend fun insertWeather(weather: CurrentWeather): Long

    suspend fun getFavoriteWeathers(): Flow<List<CurrentWeather>>

    fun getWeatherById(id: Int): Flow<CurrentWeather>

    suspend fun updateWeather(weather: CurrentWeather)

    suspend fun deleteWeather(weather: CurrentWeather)

    fun getCachedWeather(): Flow<CurrentWeather>
}
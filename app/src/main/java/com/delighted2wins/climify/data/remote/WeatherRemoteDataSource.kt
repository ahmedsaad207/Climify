package com.delighted2wins.climify.data.remote

import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.domainmodel.LocationInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {

    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String,lang: String): Flow<CurrentWeatherResponse>

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
}
package com.delighted2wins.climify.data.remote

import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.model.State

interface WeatherRemoteDataSource {

    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String): CurrentWeatherResponse

    suspend fun getUpcomingForecast(
        lat: Double,
        lon: Double,
        units: String
    ): UpcomingForecastResponse

    suspend fun getStateNameFromLocation(
        lat: Double,
        lon: Double,
    ): List<State>
}
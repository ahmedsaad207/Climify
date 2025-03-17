package com.delighted2wins.climify.data.repo

import com.delighted2wins.climify.data.remote.WeatherRemoteDataSource
import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.model.State

class WeatherRepositoryImpl(private val remote: WeatherRemoteDataSource) : WeatherRepository {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String
    ): CurrentWeatherResponse {
        return remote.getCurrentWeather(lat, lon, units)
    }

    override suspend fun getUpcomingForecast(
        lat: Double,
        lon: Double,
        units: String
    ): UpcomingForecastResponse {
        return remote.getUpcomingForecast(lat, lon, units)
    }

    override suspend fun getStateInfoByLocation(lat: Double, lon: Double): List<State> {
        return remote.getStateInfoByLocation(lat, lon)
    }

    override suspend fun getLocationByQuery(query: String): List<State> {
        return remote.getLocationByQuery(query)
    }

}
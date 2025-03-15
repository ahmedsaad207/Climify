package com.delighted2wins.climify.data.remote

import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse

class WeatherRemoteDataSourceImpl(private val service: WeatherService) : WeatherRemoteDataSource {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String
    ): CurrentWeatherResponse {
        return service.getCurrentWeather(lat, lon, units)
    }

    override suspend fun getUpcomingForecast(
        lat: Double,
        lon: Double,
        units: String
    ): UpcomingForecastResponse {
        return service.getUpcomingForecast(lat, lon, units)
    }

}
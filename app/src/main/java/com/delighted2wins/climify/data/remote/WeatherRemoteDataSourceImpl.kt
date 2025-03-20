package com.delighted2wins.climify.data.remote

import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.domainmodel.State

class WeatherRemoteDataSourceImpl(private val service: WeatherService) : WeatherRemoteDataSource {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): CurrentWeatherResponse {
        return service.getCurrentWeather(lat, lon, units, lang)
    }

    override suspend fun getUpcomingForecast(
        lat: Double,
        lon: Double,
        units: String
    ): UpcomingForecastResponse {
        return service.getUpcomingForecast(lat, lon, units)
    }

    override suspend fun getStateInfoByLocation(
        lat: Double,
        lon: Double,
    ): List<State> {
        return service.getStateInfoByLocation(lat, lon)
    }

    override suspend fun getLocationByQuery(query: String): List<State> {
        return service.getLocationByQuery(query)
    }

}
package com.delighted2wins.climify.data.remote

import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.domainmodel.LocationInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WeatherRemoteDataSourceImpl(private val service: WeatherService) : WeatherRemoteDataSource {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<CurrentWeatherResponse> {
        return flowOf(service.getCurrentWeather(lat, lon, units, lang))
    }

    override suspend fun getUpcomingForecast(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<UpcomingForecastResponse> {
        return flowOf(service.getUpcomingForecast(lat, lon, units))
    }

    override suspend fun getStateInfoByLocation(
        lat: Double,
        lon: Double,
    ): List<LocationInfo> {
        return service.getStateInfoByLocation(lat, lon)
    }

    override suspend fun getLocationByQuery(query: String): List<LocationInfo> {
        return service.getLocationByQuery(query)
    }

}
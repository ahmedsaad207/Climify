package com.delighted2wins.climify.data.repo

import com.delighted2wins.climify.data.local.WeathersLocalDataSource
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSource
import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.model.CurrentWeather
import com.delighted2wins.climify.model.State
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val remote: WeatherRemoteDataSource,
    private val local: WeathersLocalDataSource,
    ) : WeatherRepository {
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

    override suspend fun insertWeather(weather: CurrentWeather): Long {
        return local.insertWeather(weather)
    }

    override suspend fun getFavoriteWeathers(): Flow<List<CurrentWeather>> {
        return local.getFavoriteWeathers()
    }

}
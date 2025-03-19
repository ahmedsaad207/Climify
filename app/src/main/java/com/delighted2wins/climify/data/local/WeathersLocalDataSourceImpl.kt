package com.delighted2wins.climify.data.local

import com.delighted2wins.climify.model.CurrentWeather
import kotlinx.coroutines.flow.Flow

class WeathersLocalDataSourceImpl(private val dao: WeatherDao) : WeathersLocalDataSource {
    override suspend fun insertWeather(weather: CurrentWeather): Long {
        return dao.insertWeather(weather)
    }

    override suspend fun getFavoriteWeathers(): Flow<List<CurrentWeather>> {
        return dao.getFavoriteWeathers()
    }

}
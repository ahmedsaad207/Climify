package com.delighted2wins.climify.data.local

import com.delighted2wins.climify.domainmodel.CurrentWeather
import kotlinx.coroutines.flow.Flow

class WeathersLocalDataSourceImpl(private val dao: WeatherDao) : WeathersLocalDataSource {
    override suspend fun insertWeather(weather: CurrentWeather): Long {
        return dao.insertWeather(weather)
    }

    override suspend fun getFavoriteWeathers(): Flow<List<CurrentWeather>> {
        return dao.getFavoriteWeathers()
    }

    override fun getWeatherById(id: Int): Flow<CurrentWeather> {
        return dao.getWeatherById(id)
    }

    override suspend fun updateWeather(weather: CurrentWeather) {
        dao.updateWeather(weather)
    }

    override suspend fun deleteWeather(weather: CurrentWeather) {
        dao.deleteWeather(weather)
    }

}
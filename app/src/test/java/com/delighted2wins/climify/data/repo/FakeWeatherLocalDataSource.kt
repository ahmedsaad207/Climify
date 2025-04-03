package com.delighted2wins.climify.data.repo

import com.delighted2wins.climify.data.local.db.IWeathersLocalDataSource
import com.delighted2wins.climify.domainmodel.CurrentWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

class FakeWeatherLocalDataSource(val weathers: MutableList<CurrentWeather>?) : IWeathersLocalDataSource {
    override suspend fun insertWeather(weather: CurrentWeather): Long {
        return if (weathers?.add(weather) == true) 1 else 0
    }

    override suspend fun getFavoriteWeathers(): Flow<List<CurrentWeather>> {
        return flowOf(weathers ?: emptyList())
    }

    override fun getWeatherById(id: Int): Flow<CurrentWeather> {
        val weather = weathers?.find {it.id == id}
        return if (weather != null) flowOf(weather) else emptyFlow()
    }

    override suspend fun updateWeather(weather: CurrentWeather) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeather(weather: CurrentWeather) {
        TODO("Not yet implemented")
    }

    override fun getCachedWeather(): Flow<CurrentWeather> {
        TODO("Not yet implemented")
    }
}
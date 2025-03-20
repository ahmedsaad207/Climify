package com.delighted2wins.climify.data.local

import com.delighted2wins.climify.domainmodel.CurrentWeather
import kotlinx.coroutines.flow.Flow

interface WeathersLocalDataSource {
    suspend fun insertWeather(weather: CurrentWeather): Long

    suspend fun getFavoriteWeathers(): Flow<List<CurrentWeather>>

}
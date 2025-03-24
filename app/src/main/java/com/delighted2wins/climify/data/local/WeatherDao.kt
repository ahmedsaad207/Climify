package com.delighted2wins.climify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.delighted2wins.climify.domainmodel.CurrentWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: CurrentWeather): Long

    @Query("SELECT * FROM CurrentWeather")
    fun getFavoriteWeathers(): Flow<List<CurrentWeather>>

    @Query("SELECT * FROM CurrentWeather WHERE id == :id")
    fun getWeatherById(id: Int): Flow<CurrentWeather>

    @Update
    suspend fun updateWeather(weather: CurrentWeather)
}
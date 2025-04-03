package com.delighted2wins.climify.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.delighted2wins.climify.domainmodel.Alarm
import com.delighted2wins.climify.domainmodel.CurrentWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: CurrentWeather): Long

    @Query("SELECT * FROM CurrentWeather WHERE id != 1 ORDER BY dt DESC")
    fun getFavoriteWeathers(): Flow<List<CurrentWeather>>

    @Query("SELECT * FROM CurrentWeather WHERE id = 1")
    fun getCachedWeather(): Flow<CurrentWeather>

    @Query("SELECT * FROM CurrentWeather WHERE id == :id")
    fun getWeatherById(id: Int): Flow<CurrentWeather>

    @Update
    suspend fun updateWeather(weather: CurrentWeather)

    @Delete
    suspend fun deleteWeather(weather: CurrentWeather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm): Long

    @Query("SELECT * FROM alarms  ORDER BY tag DESC")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Delete
    suspend fun deleteAlarm(alarm: Alarm): Int
}
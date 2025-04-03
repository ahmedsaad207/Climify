package com.delighted2wins.climify.data.repo

import com.delighted2wins.climify.data.local.db.AlarmsLocalDataSource
import com.delighted2wins.climify.data.local.db.IWeathersLocalDataSource
import com.delighted2wins.climify.data.local.preferences.PreferencesDataSource
import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSource
import com.delighted2wins.climify.domainmodel.Alarm
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.LocationInfo
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val remote: WeatherRemoteDataSource,
    private val weatherLocal: IWeathersLocalDataSource,
    private val alarmLocal: AlarmsLocalDataSource,
    private val preferences: PreferencesDataSource
    ) : WeatherRepository {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<CurrentWeatherResponse> {
        return remote.getCurrentWeather(lat, lon, units, lang)
    }

    override suspend fun getUpcomingForecast(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<UpcomingForecastResponse> {
        return remote.getUpcomingForecast(lat, lon, units)
    }

    override suspend fun getStateInfoByLocation(lat: Double, lon: Double): List<LocationInfo> {
        return remote.getStateInfoByLocation(lat, lon)
    }

    override suspend fun getLocationByQuery(query: String): List<LocationInfo> {
        return remote.getLocationByQuery(query)
    }

    override suspend fun insertWeather(weather: CurrentWeather): Long {
        return weatherLocal.insertWeather(weather)
    }

    override suspend fun getFavoriteWeathers(): Flow<List<CurrentWeather>> {
        return weatherLocal.getFavoriteWeathers()
    }

    override fun getWeatherById(id: Int): Flow<CurrentWeather> {
        return weatherLocal.getWeatherById(id)
    }

    override suspend fun updateWeather(weather: CurrentWeather) {
        weatherLocal.updateWeather(weather)
    }

    override suspend fun deleteWeather(weather: CurrentWeather) {
        weatherLocal.deleteWeather(weather)
    }

    override fun <T> saveData(value: T) {
        preferences.saveData(value)
    }

    override fun <T> getData(type: String): T {
        return preferences.getData(type)
    }

    override suspend fun insertAlarm(alarm: Alarm): Long {
        return alarmLocal.insertAlarm(alarm)
    }

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmLocal.getAllAlarms()
    }

    override suspend fun deleteAlarm(alarm: Alarm): Int {
        return alarmLocal.deleteAlarm(alarm)
    }

    override fun getCachedWeather(): Flow<CurrentWeather> {
        return weatherLocal.getCachedWeather()
    }

}
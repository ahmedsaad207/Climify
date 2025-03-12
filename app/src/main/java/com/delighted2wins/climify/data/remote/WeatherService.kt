package com.delighted2wins.climify.data.remote

import com.delighted2wins.climify.utils.OPEN_WEATHER_API
import com.delighted2wins.climify.model.ForecastResult
import com.delighted2wins.climify.model.WeatherResult
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") appid: String = OPEN_WEATHER_API
    ): WeatherResult

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") appid: String = OPEN_WEATHER_API
    ): ForecastResult
}
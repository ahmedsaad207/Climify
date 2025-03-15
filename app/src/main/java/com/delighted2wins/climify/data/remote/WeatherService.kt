package com.delighted2wins.climify.data.remote

import com.delighted2wins.climify.BuildConfig
import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.utils.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") appid: String = BuildConfig.ApiKey
    ): CurrentWeatherResponse

    @GET("forecast")
    suspend fun getUpcomingForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") appid: String = BuildConfig.ApiKey
    ): UpcomingForecastResponse
}
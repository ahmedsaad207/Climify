package com.delighted2wins.climify.data.remote

import com.delighted2wins.climify.BuildConfig
import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.domainmodel.State
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("appid") appid: String = BuildConfig.ApiKey
    ): CurrentWeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getUpcomingForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") appid: String = BuildConfig.ApiKey
    ): UpcomingForecastResponse

    @GET("geo/1.0/reverse")
    suspend fun getStateInfoByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String = BuildConfig.ApiKey
    ): List<State>

    @GET("geo/1.0/direct")
    suspend fun getLocationByQuery(
        @Query("q") query: String,
        @Query("appid") appid: String = BuildConfig.ApiKey
    ): List<State>
}
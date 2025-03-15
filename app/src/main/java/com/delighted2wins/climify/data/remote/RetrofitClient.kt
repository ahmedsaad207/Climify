package com.delighted2wins.climify.data.remote

import com.delighted2wins.climify.utils.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: WeatherService = retrofit.create(WeatherService::class.java)
}
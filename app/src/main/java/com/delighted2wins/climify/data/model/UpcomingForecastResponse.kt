package com.delighted2wins.climify.data.model

import com.google.gson.annotations.SerializedName


data class UpcomingForecastResponse(
    @SerializedName("cod") var cod: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("cnt") var cnt: Int? = null,
    @SerializedName("list") var forecastWeatherList: ArrayList<NetworkForecastWeather> = arrayListOf(),
    @SerializedName("city") var city: City? = City()
)
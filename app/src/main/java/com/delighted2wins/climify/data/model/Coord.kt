package com.delighted2wins.climify.data.model

import com.google.gson.annotations.SerializedName


data class Coord(
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lon") var lon: Double? = null
)
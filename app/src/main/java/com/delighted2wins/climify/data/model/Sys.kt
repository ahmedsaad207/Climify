package com.delighted2wins.climify.data.model

import com.google.gson.annotations.SerializedName


data class Sys(
    @SerializedName("pod") var pod: String? = null,
    @SerializedName("sunrise") var sunrise: Int? = null,
    @SerializedName("sunset") var sunset: Int? = null
)
package com.delighted2wins.climify.data.model

import com.google.gson.annotations.SerializedName


data class Clouds(
    @SerializedName("all") var all: Int? = null
)
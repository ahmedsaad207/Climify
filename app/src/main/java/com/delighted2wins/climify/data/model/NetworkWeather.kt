package com.delighted2wins.climify.data.model

import com.google.gson.annotations.SerializedName

data class NetworkWeather(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("main") var main: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("icon") var icon: String? = null
)
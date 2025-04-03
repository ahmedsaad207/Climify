package com.delighted2wins.climify.domainmodel

import com.google.gson.annotations.SerializedName

data class LocationInfo(

    @SerializedName("name") var name: String? = null,
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lon") var lon: Double? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("local_names") var localNames: LocalNames? = LocalNames()
)

data class LocalNames(
    @SerializedName("en" ) var en : String? = null,
    @SerializedName("ar" ) var ar : String? = null
)

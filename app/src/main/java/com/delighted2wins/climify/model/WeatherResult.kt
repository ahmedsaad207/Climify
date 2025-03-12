package com.delighted2wins.climify.model

data class WeatherResult(
    var coord: Coord? = Coord(),
    var weather: List<Weather>? = listOf(),
    var base: String? = null,
    var main: Main? = Main(),
    var visibility: Int? = null,
    var wind: Wind? = Wind(),
    var clouds: Clouds? = Clouds(),
    var dt: Int? = null,
    var sys: Sys? = Sys(),
    var timezone: Int? = null,
    var id: Int? = null,
    var name: String? = null,
    var cod: Int? = null,
)
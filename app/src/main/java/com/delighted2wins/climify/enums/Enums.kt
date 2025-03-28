package com.delighted2wins.climify.enums

enum class TempUnit(val value: String) {
    STANDARD("standard"),
    METRIC("metric"),
    IMPERIAL("imperial")
}

enum class Language(val value: String) {
    EN("en"),
    AR("ar"),
    DEFAULT("default")
}

enum class LocationSource(val value: String) {
    MAP("map"),
    GPS("gps")
}

enum class WindSpeedUnit(val value: String) {
    STANDARD("standard"),
    METRIC("metric"),
    IMPERIAL("imperial")
}

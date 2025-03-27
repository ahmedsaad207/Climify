package com.delighted2wins.climify.enums

enum class Language(val value: String) {
    EN("en"),
    AR("ar"),
    DEFAULT("default")
}

enum class TempUnit(val value: String) {
    STANDARD("standard"),
    METRIC("metric"),
    IMPERIAL("imperial")
}

enum class WindSpeedUnit(val value: String) {
    STANDARD("standard"),
    IMPERIAL("imperial")
}

enum class LocationSource(val value: String) {
    MAP("map"),
    GPS("gps")
}
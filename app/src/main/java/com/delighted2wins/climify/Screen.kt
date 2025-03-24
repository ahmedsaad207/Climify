package com.delighted2wins.climify

import com.delighted2wins.climify.domainmodel.CurrentWeather
import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Favorite : Screen()

    @Serializable
    data object Alarm : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object LocationSelection : Screen()

    @Serializable
    data class Details(val id: Int) : Screen()
}
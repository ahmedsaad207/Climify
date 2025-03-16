package com.delighted2wins.climify

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
}
package com.delighted2wins.climify.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.ForecastWeather

@Composable
fun DisplayHomeData(
    currentWeather: CurrentWeather,
    onNavigateToLocationSelection: () -> Unit,
    forecastHours: List<ForecastWeather>,
    forecastDays: List<ForecastWeather>
) {
    Box {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xff151513))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayCurrentWeather(onNavigateToLocationSelection, currentWeather)
            DisplayHourlyWeathers(forecastHours)
            DisplayUpcomingWeathers(forecastDays)
        }


    }
}
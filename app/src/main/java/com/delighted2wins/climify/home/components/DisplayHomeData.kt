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
import androidx.compose.ui.res.colorResource
import com.delighted2wins.climify.R
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.ForecastWeather

@Composable
fun DisplayHomeData(
    currentWeather: CurrentWeather,
    onNavigateToLocationSelection: () -> Unit = {},
    forecastHours: List<ForecastWeather>,
    forecastDays: List<ForecastWeather>,
    isOnline: Boolean = true,
    backButton: Boolean = false,
    onNavigateBack: () -> Unit = {}
) {
    Box {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.deep_gray))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isOnline) {
                DisplayCurrentWeather(onNavigateToLocationSelection, currentWeather, backButton, onNavigateBack)
                DisplayHourlyWeathers(forecastHours)
                DisplayUpcomingWeathers(forecastDays)
            } else {
                DisplayCurrentWeather(onNavigateToLocationSelection, currentWeather,backButton, onNavigateBack, true)
            }
        }


    }
}
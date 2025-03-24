package com.delighted2wins.climify.weatherdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.Response
import com.delighted2wins.climify.home.components.DisplayHomeData
import com.delighted2wins.climify.home.components.LoadingIndicator
import com.delighted2wins.climify.home.getRepo

@Composable
fun DetailsUI(
    id: Int,
    showBottomNabBar: MutableState<Boolean>,
    showFloatingActionButton: MutableState<Boolean>,
    onNavigateBack: () -> Unit
) {

    val context = LocalContext.current
    val viewModel: WeatherDetailsViewModel =
        viewModel(factory = WeatherDetailsViewModelFactory(getRepo(context)))
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        showBottomNabBar.value = false
        showFloatingActionButton.value = false
        viewModel.getWeatherById(id)
    }

    // check internet if internet available get from api all data if not get from room
    val isInternetAvailable = true

    LaunchedEffect(weather) {
        weather?.let {
            if (isInternetAvailable) {
                viewModel.fetchWeatherData(it.lat, it.long, id = it.id)
            } else {
                viewModel.fetchLocalData(it)
            }
        }
    }

    when (uiState) {
        is Response.Loading -> LoadingIndicator()

        is Response.Success -> {
            val (currentWeather, forecastHours, forecastDays) = (uiState as Response.Success).data
            Column {
                DisplayHomeData(
                    currentWeather,
                    forecastHours = forecastHours,
                    forecastDays = forecastDays,
                    isOnline = isInternetAvailable,
                    backButton = true,
                    onNavigateBack = onNavigateBack
                )
            }
        }

        is Response.Failure -> {
            // TODO Show Image with Text
            val error = (uiState as Response.Failure).error
            Text(error)
        }
    }
}

@Composable
fun BackButton(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(Color.Black.copy(0.4f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = { onNavigateBack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}

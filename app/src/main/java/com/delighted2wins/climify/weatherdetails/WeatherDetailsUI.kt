package com.delighted2wins.climify.weatherdetails

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var isInternetAvailable by remember { mutableStateOf(true) }

    LaunchedEffect(weather) {
        weather?.let {
            viewModel.fetchWeatherData(it, isOnline = isInternetAvailable)
        }
    }

    when (uiState) {
        is Response.Loading -> LoadingIndicator()

        is Response.Success -> {
            Log.i("TAG", "fetchWeatherData: success")

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
            if (isInternetAvailable) {
                isInternetAvailable = false
                weather?.let {
                    viewModel.fetchWeatherData(it, isOnline = false)
                }
            } else {
                // TODO Error
            }

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

package com.delighted2wins.climify.features.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.domainmodel.Response
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.features.home.components.DisplayHomeData
import com.delighted2wins.climify.features.home.components.LoadingIndicator
import com.delighted2wins.climify.features.home.getRepo
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.NetworkManager
import kotlinx.coroutines.launch

@Composable
fun DetailsUI(
    id: Int,
    showBottomNabBar: MutableState<Boolean>,
    showFloatingActionButton: MutableState<Boolean>,
    snackBarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit
) {
    var flag by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val viewModel: WeatherDetailsViewModel =
        viewModel(factory = WeatherDetailsViewModelFactory(getRepo(context)))
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val networkManager = NetworkManager(context)
    val isOnline by networkManager.observeNetworkChanges()
        .collectAsStateWithLifecycle(networkManager.isNetworkAvailable())

    LaunchedEffect(Unit) {
        showBottomNabBar.value = false
        showFloatingActionButton.value = false
        viewModel.getWeatherById(id)
    }

    LaunchedEffect(weather) {
        weather?.let {
            viewModel.fetchWeatherData(it, isOnline = isOnline)
        }
    }

    LaunchedEffect(isOnline) {
        val message =
            if (isOnline) (if (flag) "" else "Your Internet connection has been restored.") else "You are currently offline."
        if (message.isNotBlank()) {
            flag = false
            scope.launch {
                snackBarHostState.currentSnackbarData?.dismiss()
                snackBarHostState.showSnackbar(
                    message,
                    null,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    when (uiState) {
        is Response.Loading -> LoadingIndicator()

        is Response.Success -> {
            val (currentWeather, forecastHours, forecastDays) = (uiState as Response.Success).data
            val appUnit = viewModel.getData<TempUnit>(Constants.KEY_TEMP_UNIT).value
            Column {
                DisplayHomeData(
                    currentWeather,
                    forecastHours = forecastHours,
                    forecastDays = forecastDays,
                    isOnline = isOnline,
                    backButton = true,
                    onNavigateBack = onNavigateBack,
                    appUnit = appUnit
                )
            }
        }

        is Response.Failure -> {
            if (isOnline) {
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
            .size(48.dp)
            .background(Color.Black.copy(0.4f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = { onNavigateBack() },
            modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

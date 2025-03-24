package com.delighted2wins.climify.home

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.Response
import com.delighted2wins.climify.data.local.WeatherDatabase
import com.delighted2wins.climify.data.local.WeathersLocalDataSourceImpl
import com.delighted2wins.climify.data.remote.RetrofitClient
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSourceImpl
import com.delighted2wins.climify.data.repo.WeatherRepositoryImpl
import com.delighted2wins.climify.home.components.DisplayHomeData
import com.delighted2wins.climify.home.components.LoadingIndicator
import com.delighted2wins.climify.utils.SP_NAME

@Composable
fun HomeUi(
    showBottomNabBar: MutableState<Boolean>,
    onNavigateToLocationSelection: () -> Unit
) {
    showBottomNabBar.value = true

    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(getRepo(context))
    )
    val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    val lat = sharedPreferences.getString("lat", "0.0") ?: "0.0"
    val lon = sharedPreferences.getString("lon", "0.0") ?: "0.0"
    val lang = sharedPreferences.getString("lang", "en") ?: "en"
    val tempUnit = sharedPreferences.getString("temp", "metric") ?: "metric"

    LaunchedEffect(Unit) {
        viewModel.fetchWeatherData(lat.toDouble(), lon.toDouble(), tempUnit, lang)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    when (uiState) {
        is Response.Loading -> LoadingIndicator()

        is Response.Success -> {
            val (currentWeather, forecastHours, forecastDays) = (uiState as Response.Success).data
            DisplayHomeData(
                currentWeather!!,
                onNavigateToLocationSelection,
                forecastHours,
                forecastDays
            )
        }

        is Response.Failure -> {
            // TODO Show Image with Text
            val error = (uiState as Response.Failure).error
            Text(error)
        }
    }
}

fun getRepo(context: Context) = WeatherRepositoryImpl(
    WeatherRemoteDataSourceImpl(RetrofitClient.service),
    WeathersLocalDataSourceImpl(
        WeatherDatabase.getInstance(context.applicationContext).getWeatherDao(),
    )
)











package com.delighted2wins.climify.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.delighted2wins.climify.R
import com.delighted2wins.climify.Response
import com.delighted2wins.climify.data.local.db.WeatherDatabase
import com.delighted2wins.climify.data.local.db.WeathersLocalDataSourceImpl
import com.delighted2wins.climify.data.local.preferences.PreferencesDataSourceImpl
import com.delighted2wins.climify.data.remote.RetrofitClient
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSourceImpl
import com.delighted2wins.climify.data.repo.WeatherRepositoryImpl
import com.delighted2wins.climify.enums.Language
import com.delighted2wins.climify.enums.LocationSource
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.home.components.DisplayHomeData
import com.delighted2wins.climify.home.components.LoadingIndicator
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.getUserLocationUsingGps

@Composable
fun HomeUi(
    showBottomNabBar: MutableState<Boolean>, onNavigateToLocationSelection: () -> Unit
) {
    showBottomNabBar.value = true

    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(getRepo(context))
    )

    val lang = viewModel.getData<Language>(Constants.KEY_LANG).value
    val tempUnit = viewModel.getData<TempUnit>(Constants.KEY_TEMP_UNIT).value
    val userLocation = viewModel.getData<LocationSource>(Constants.KEY_LOCATION_SOURCE).value


    LaunchedEffect(Unit) {
        if (userLocation == LocationSource.MAP.value) {
            val (lat, lon) = viewModel.getData<Pair<Double, Double>>("")
            viewModel.fetchWeatherData(lat, lon, tempUnit, lang)
        } else {
            context.getUserLocationUsingGps { lat, lon ->
                viewModel.fetchWeatherData(lat, lon, tempUnit, lang)
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    when (uiState) {
        is Response.Loading -> LoadingIndicator()

        is Response.Success -> {
            val (currentWeather, forecastHours, forecastDays) = (uiState as Response.Success).data
            DisplayHomeData(
                currentWeather!!, onNavigateToLocationSelection, forecastHours, forecastDays
            )
        }

        is Response.Failure -> {
            val error = (uiState as Response.Failure).error
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error3))
            val progress by animateLottieCompositionAsState(
                composition = composition, iterations = 1
            )
            Column {
                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(R.color.deep_gray)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnimation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        composition = composition,
                        progress = { progress },
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = error,
                        fontSize = 18.sp,
                        color = Color(0xFF808080),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun getRepo(context: Context) = WeatherRepositoryImpl(
    WeatherRemoteDataSourceImpl(RetrofitClient.service), WeathersLocalDataSourceImpl(
        WeatherDatabase.getInstance(context.applicationContext).getWeatherDao()
    ), PreferencesDataSourceImpl(
        context.getSharedPreferences(
            Constants.PREF_NAME, Context.MODE_PRIVATE
        )
    )
)

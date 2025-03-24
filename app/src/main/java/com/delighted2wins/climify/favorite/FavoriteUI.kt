package com.delighted2wins.climify.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.delighted2wins.climify.R
import com.delighted2wins.climify.Response
import com.delighted2wins.climify.data.local.WeatherDatabase
import com.delighted2wins.climify.data.local.WeathersLocalDataSourceImpl
import com.delighted2wins.climify.data.remote.RetrofitClient
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSourceImpl
import com.delighted2wins.climify.data.repo.WeatherRepositoryImpl
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.home.components.LoadingIndicator
import com.delighted2wins.climify.utils.getCountryNameFromCode
import com.delighted2wins.climify.utils.timeStampToHumanDate

@Composable
fun FavoriteUI(
    showBottomNabBar: MutableState<Boolean>,
    onNavigateToWeatherDetails: () -> Unit
) {
    showBottomNabBar.value = true
    val context = LocalContext.current
    val viewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(
            WeatherRepositoryImpl(
                WeatherRemoteDataSourceImpl(RetrofitClient.service),
                WeathersLocalDataSourceImpl(
                    WeatherDatabase.getInstance(context.applicationContext).getWeatherDao()
                )
            )
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is Response.Loading -> LoadingIndicator()

        is Response.Success -> {
            val weathers = (uiState as Response.Success).data

            if (weathers.isNotEmpty()) {    // show data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    item {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Favorite Locations",
                            fontSize = 24.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    }
                    items(weathers.size) {
                        FavoriteLocationItem(weathers[it], onNavigateToWeatherDetails)
                    }

                }
            } else {    // empty list
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty2))
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = 1
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
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
                        text = "No favorite locations found. Add some to get started!",
                        fontSize = 18.sp,
                        color = Color(0xFF808080),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

        }

        is Response.Failure -> {

            val error = (uiState as Response.Failure).error
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error3))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = 1
            )

            Column {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Favorite Locations",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                )
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
                        text = error,
                        fontSize = 18.sp,
                        color = Color(0xFF808080)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FavoriteLocationItem(weather: CurrentWeather, onNavigateToWeatherDetails: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .background(Color(0xff1E1F1C), shape = RoundedCornerShape(32.dp))
            .padding(16.dp)
            .clickable { onNavigateToWeatherDetails() }
    ) {
        // country, city and description, time
        Column {
            Text(
                text = getCountryNameFromCode(weather.country) ?: "",
                fontSize = 18.sp,
                color = Color(0xFF808080),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = weather.city,
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = weather.description,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Last update: ${
                    timeStampToHumanDate(
                        weather.dt.toLong(),
                        "\ndd MMM, hh:mm a"
                    )
                }",
                fontSize = 14.sp,
                color = Color(0xFF808080),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
            )

        }
        Spacer(Modifier.weight(1f))

        // icon and temp
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlideImage(
                model = weather.icon,
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )
            Text(
                text = "${weather.temp.toInt()} °C",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

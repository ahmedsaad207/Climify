@file:OptIn(ExperimentalGlideComposeApi::class)

package com.delighted2wins.climify.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.delighted2wins.climify.utils.timeStampToHumanDate

@Composable
fun HomeUi(innerPadding: PaddingValues, viewModel: WeatherViewModel) {
    val currentWeatherState = viewModel.currentWeatherLiveData.observeAsState()
    val hourlyForecastWeatherListState = viewModel.hourlyForecastWeatherList.observeAsState()
    val upcomingDaysForecastWeatherListState =
        viewModel.upcomingDaysForecastWeatherList.observeAsState()


    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // City, Date
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.padding(start = 16.dp, end = 4.dp)
            )
            Text(
                text = currentWeatherState.value?.city ?: "",
                fontSize = 24.sp,
                modifier = Modifier.wrapContentWidth(),
                fontWeight = FontWeight.Medium
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Column {
                Text(
                    text = "Today",
                    fontSize = 40.sp,
                    modifier = Modifier.padding(end = 24.dp),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = currentWeatherState.value?.dateText ?: ""/*"Fri, 18 Feb"*/,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(end = 24.dp, top = 4.dp),
                    color = Color.Black
                )
            }
        }
        // Time
        Text(
            text = currentWeatherState.value?.timeText ?: "", fontSize = 32.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp),
        )

        // Icon
        GlideImage(
            model = currentWeatherState.value?.icon,
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )

        // Temp
        Text(
            text = "${currentWeatherState.value?.temp?.toInt()}\u00B0",
            fontSize = 63.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 6.dp),
        )
        // Description
        Text(
            text = currentWeatherState.value?.description ?: "",
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )

        // Weather Details
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailsSection(
                currentWeatherState.value?.icon ?: "",
                currentWeatherState.value?.humidity ?: "0 %",
                "Humidity"
            )
            WeatherDetailsSection(
                currentWeatherState.value?.icon ?: "",
                currentWeatherState.value?.pressure ?: "0 hPa",
                "Pressure"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailsSection(
                currentWeatherState.value?.icon ?: "",
                "${currentWeatherState.value?.windSpeed} m/s",
                "Wind"
            )
            WeatherDetailsSection(
                currentWeatherState.value?.icon ?: "",
                currentWeatherState.value?.cloud ?: "0 %",
                "Clouds"
            )
        }

        // Hourly Forecast
        Text(
            text = "Hourly Forecast",
            fontSize = 24.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(hourlyForecastWeatherListState.value?.size ?: 0) {
                HourlyForecastSection(
                    hourlyForecastWeatherListState.value?.get(it)?.icon ?: "",
                    hourlyForecastWeatherListState.value?.get(it)?.time ?: "",
                    "${hourlyForecastWeatherListState.value?.get(it)?.temp?.toInt()}°"
                )
            }
        }

        // Next 4 Days
        Text(
            text = "Upcoming Forecast",
            fontSize = 24.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )
        var tempMin: String
        var tempMax: String
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(upcomingDaysForecastWeatherListState.value?.size ?: 0) { i ->
                tempMin = if (upcomingDaysForecastWeatherListState.value?.get(i)?.tempMin != null) {
                    upcomingDaysForecastWeatherListState.value?.get(i)?.tempMin?.toInt()
                        .toString() + "°"
                } else "0°"
                tempMax = if (upcomingDaysForecastWeatherListState.value?.get(i)?.tempMax != null) {
                    upcomingDaysForecastWeatherListState.value?.get(i)?.tempMax?.toInt()
                        .toString() + "°"
                } else "0°"

                UpcomingForecastSection(
                    timeStampToHumanDate(
                        upcomingDaysForecastWeatherListState.value?.get(i)?.date?.toLong() ?: 0L,
                        "EEE"
                    ),
                    timeStampToHumanDate(
                        upcomingDaysForecastWeatherListState.value?.get(i)?.date?.toLong() ?: 0L,
                        "d MMM"
                    ),
                    upcomingDaysForecastWeatherListState.value?.get(i)?.icon ?: "",
                    upcomingDaysForecastWeatherListState.value?.get(i)?.description ?: "",
                    "$tempMin-$tempMax"
                )
            }
        }


    }
}

@Composable
fun WeatherDetailsSection(icon: String, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GlideImage(
            model = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        // Temp
        Text(
            text = value,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 6.dp),
        )
        // Description
        Text(
            text = label,
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun HourlyForecastSection(icon: String, time: String, temp: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // time
        Text(
            text = time,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 6.dp),
        )

        // icon
        GlideImage(
            model = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )

        // temp
        Text(
            text = temp,
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun UpcomingForecastSection(
    dayInWeek: String,
    date: String,
    icon: String,
    description: String,
    temp: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // dayInWeek
        Text(
            text = dayInWeek,
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 6.dp),
        )

        // date
        Text(
            text = date,
            fontSize = 18.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 4.dp),
        )
        // icon
//        Image(
//            painter = painterResource(icon),
//            contentDescription = null,
//            modifier = Modifier.size(48.dp),
//        )
        // icon
        GlideImage(
            model = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        // description
        Text(
            text = description,
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp),
        )

        // temp
        Text(
            text = temp,
            fontSize = 14.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
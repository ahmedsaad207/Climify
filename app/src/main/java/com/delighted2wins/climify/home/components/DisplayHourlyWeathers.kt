package com.delighted2wins.climify.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delighted2wins.climify.domainmodel.ForecastWeather

@Composable
fun DisplayHourlyWeathers(forecastHours: List<ForecastWeather>) {

    Spacer(Modifier.height(24.dp))
    // Hourly Forecast
    Text(
        text = "Today",
        fontSize = 24.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp),
//        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(forecastHours.size) {
            HourlyForecastItem(
                forecastHours[it].icon,
                forecastHours[it].time,
                "${forecastHours[it].temp.toInt()}°"
            )
        }
    }
}
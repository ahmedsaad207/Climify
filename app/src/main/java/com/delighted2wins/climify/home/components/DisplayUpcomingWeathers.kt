package com.delighted2wins.climify.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.delighted2wins.climify.domainmodel.ForecastWeather
import com.delighted2wins.climify.utils.timeStampToHumanDate

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DisplayUpcomingWeathers(forecastDays: List<ForecastWeather>) {
    Spacer(Modifier.height(24.dp))
    Text(
        text = "Upcoming Forecast",
        fontSize = 24.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    )
    var tempMin: String
    var tempMax: String
    Spacer(Modifier.height(24.dp))

    Column {
        forecastDays.forEach {
            UpcomingForecastItem(
                it.date.toLong(),
                it.icon,
                it.temp.toInt(),
                "C"
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    /*LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(forecastDays.size) { i ->
            tempMin = forecastDays[i].tempMin.toInt()
                .toString() + "°"
            tempMax = forecastDays[i].tempMax.toInt()
                .toString() + "°"

            UpcomingForecastItem(
                timeStampToHumanDate(
                    forecastDays[i].date.toLong(),
                    "EEE"
                ),
                timeStampToHumanDate(
                    forecastDays[i].date.toLong(),
                    "d MMM"
                ),
                forecastDays[i].icon,
                forecastDays[i].description,
                "$tempMin-$tempMax"
            )
        }
    }*/

    Spacer(Modifier.height(8.dp))
}
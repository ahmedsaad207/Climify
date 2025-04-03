package com.delighted2wins.climify.features.home.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delighted2wins.climify.R
import com.delighted2wins.climify.domainmodel.ForecastWeather
import com.delighted2wins.climify.utils.getTempUnitSymbol

@Composable
fun DisplayHourlyWeathers(forecastHours: List<ForecastWeather>) {
    val context = LocalContext.current

    Spacer(Modifier.height(24.dp))
    // Hourly Forecast
    Text(
        text = stringResource(R.string.today),
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
    ) {
        items(forecastHours.size) {
            HourlyForecastItem(
                forecastHours[it].icon,
                forecastHours[it].time,
                forecastHours[it].temp,
                context.getTempUnitSymbol(forecastHours[it].unit)
            )
        }
    }
}
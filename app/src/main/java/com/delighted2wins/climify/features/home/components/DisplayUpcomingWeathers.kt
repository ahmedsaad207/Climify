package com.delighted2wins.climify.features.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun DisplayUpcomingWeathers(forecastDays: List<ForecastWeather>) {
    val context = LocalContext.current
    Spacer(Modifier.height(24.dp))
    Text(
        text = stringResource(R.string.upcoming_forecast),
        fontSize = 24.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    )
    Spacer(Modifier.height(24.dp))

    Column {
        forecastDays.forEach {
            UpcomingForecastItem(
                it.date.toLong(),
                it.icon,
                it.temp,
                context.getTempUnitSymbol(it.unit)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    Spacer(Modifier.height(120.dp))
}
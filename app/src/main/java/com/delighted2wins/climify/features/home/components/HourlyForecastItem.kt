package com.delighted2wins.climify.features.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.delighted2wins.climify.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HourlyForecastItem(icon: Int, time: String, temp: String, unit: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(end = 16.dp)
    ) {

        // time
        Text(
            text = if (time == "Now") stringResource(R.string.now) else time,
            fontSize = 14.sp,
            color = colorResource(R.color.neutral_gray),
            modifier = Modifier.padding(top = 6.dp),
        )

        // icon
        GlideImage(
            model = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 4.dp)
                .size(48.dp)
        )
        // temp
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
        ) {
            Text(
                text = temp,
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = unit,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Top).padding(4.dp)
            )
        }
    }
}
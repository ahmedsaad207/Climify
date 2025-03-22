package com.delighted2wins.climify.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HourlyForecastItem(icon: Int, time: String, temp: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(end = 16.dp)
    ) {

        // time
        Text(
            text = time,
            fontSize = 14.sp,
            color = Color(0xFF808080),
            modifier = Modifier.padding(top = 6.dp),
        )

        // icon
        GlideImage(
            model = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        // temp
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = temp,
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "C",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterVertically)

            )

        }
    }
}
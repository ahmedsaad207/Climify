package com.delighted2wins.climify.home.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.delighted2wins.climify.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherDetailsSection(
    icon: Int,
    value: String,
    label: String,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        GlideImage(
            model = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        // value
        Row {
            Text(
                text = value,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 6.dp, end = 6.dp),
            )
            Text(
                text = unit,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .align(Alignment.Bottom)
            )
        }
        // Description
        Text(
            text = label,
            fontSize = 20.sp,
            color = colorResource(R.color.neutral_gray),
            fontWeight = FontWeight.Normal
        )
    }
}
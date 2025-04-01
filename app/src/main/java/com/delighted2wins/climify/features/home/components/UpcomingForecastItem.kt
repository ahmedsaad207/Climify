package com.delighted2wins.climify.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.delighted2wins.climify.utils.timeStampToHumanDate

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UpcomingForecastItem(
    date: Long,
    icon: Int,
    temp: String,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(colorResource(R.color.grayish_green), shape = RoundedCornerShape(24.dp))
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // date
            Column(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    text = timeStampToHumanDate(date, stringResource(R.string.short_day_format)),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = timeStampToHumanDate(date, stringResource(R.string.month_name_day)),
                    fontSize = 16.sp,
                    color = colorResource(R.color.neutral_gray),
                    fontWeight = FontWeight.Medium
                )
            }

            // temp
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = temp,
                    fontSize = 50.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = unit,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.align(Alignment.Top)
                    )

            }
            GlideImage(
                model = icon,
                contentDescription = null,
                Modifier.padding(6.dp)
                    .padding(6.dp)
                    .size(70.dp)
            )
        }
    }
}
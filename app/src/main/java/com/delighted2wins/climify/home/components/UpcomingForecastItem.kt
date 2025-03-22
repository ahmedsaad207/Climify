package com.delighted2wins.climify.home.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.delighted2wins.climify.utils.timeStampToHumanDate

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UpcomingForecastItem(
    date: Long,
    icon: Int,
    temp: Int,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(Color(0xff1E1F1C), shape = RoundedCornerShape(24.dp))
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // date
            Column(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    text = timeStampToHumanDate(date, "EEE"),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = timeStampToHumanDate(date, "MMMM, dd"),
                    fontSize = 14.sp,
                    color = Color(0xFF808080),
                    fontWeight = FontWeight.Medium
                )
            }

            // temp
            Row(
                verticalAlignment =Alignment.CenterVertically
            ) {
                Text(
                    text = "$temp°",
                    fontSize = 45.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = unit,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal,

                    )

            }
            GlideImage(
                model = icon,
                contentDescription = null,
                Modifier.padding(6.dp)
            )
        }

        /*

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
        )*/
    }
}
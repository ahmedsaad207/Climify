package com.delighted2wins.climify.home.components

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.delighted2wins.climify.R
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.utils.convertTemp
import com.delighted2wins.climify.utils.convertWindSpeed
import com.delighted2wins.climify.utils.getTempUnitSymbol
import com.delighted2wins.climify.utils.getWindSpeedUnitSymbol
import com.delighted2wins.climify.utils.toLocalizedNumber
import com.delighted2wins.climify.weatherdetails.BackButton
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DisplayCurrentWeather(
    onNavigateToLocationSelection: (Boolean) -> Unit = {},
    currentWeather: CurrentWeather,
    backButton: Boolean,
    onNavigateBack: () -> Unit = {},
    isLocal: Boolean = false,
    appUnit: String
) {
    val context = LocalContext.current
    val temp: String
    val unit: String
    val windSpeedValue: String
    val windSpeedUnit: String

    if (isLocal) {
        unit = context.getTempUnitSymbol(appUnit)
        windSpeedUnit = context.getWindSpeedUnitSymbol(appUnit)
        temp = currentWeather.unit.convertTemp(currentWeather.temp.toDouble(), appUnit).toInt()
            .toLocalizedNumber()

        windSpeedValue =
            currentWeather.unit.convertWindSpeed(currentWeather.windSpeed, appUnit).toInt()
                .toLocalizedNumber()
    } else {
        temp = currentWeather.temp
        unit = context.getTempUnitSymbol(currentWeather.unit)
        windSpeedUnit = context.getWindSpeedUnitSymbol(currentWeather.unit)
        windSpeedValue = currentWeather.windSpeed.toInt().toLocalizedNumber()
    }

    val pressure = currentWeather.pressure.trim().toInt().toLocalizedNumber()
    val cloud = currentWeather.cloud.trim().toInt().toLocalizedNumber()
    val humidity = currentWeather.humidity.trim().toInt().toLocalizedNumber()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // background gif
        Surface(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = rememberDrawablePainter(
                    drawable = AppCompatResources.getDrawable(
                        LocalContext.current,
                        currentWeather.background
                    )
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(300.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xff151513))
                        )
                    )
            )
        }


        // weather data
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 16.dp)
                    .size(if (backButton) 60.dp else 0.dp)
            )
            { BackButton(onNavigateBack) }

            // City
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(vertical = 24.dp, horizontal = 6.dp)
                    .align(if (backButton) Alignment.CenterHorizontally else Alignment.Start)
                    .clickable {
                        onNavigateToLocationSelection(false)
                    }
                    .background(Color.Black.copy(0.4f), shape = RoundedCornerShape(8.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 16.dp, end = 4.dp),
                    tint = colorResource(R.color.white)
                )
                Text(
                    text = currentWeather.city,
                    fontSize = 20.sp,
                    modifier = Modifier.wrapContentWidth(),
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.white)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
            }
            if (isLocal) {
                Text(
                    text = stringResource(R.string.last_update),
                    fontSize = 14.sp,
                    color = colorResource(R.color.white),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 48.dp, top = 24.dp)
                        .background(Color.Black.copy(0.4f), shape = RoundedCornerShape(8.dp))
                        .align(Alignment.Start)
                        .padding(4.dp),
                    textAlign = TextAlign.Start
                )
            }

            // Time & Date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentWeather.timeText, fontSize = 32.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 24.dp),
                    color = colorResource(R.color.white)
                )
                Column {
                    Text(
                        text = stringResource(R.string.today),
                        fontSize = 40.sp,
                        modifier = Modifier.padding(end = 24.dp),
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.white)
                    )
                    Text(
                        text = currentWeather.dateText,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(end = 24.dp, top = 4.dp),
                        color = Color.White
                    )
                }
            }

            //  weather icon
            GlideImage(
                model = currentWeather.icon,
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )

            // Temp
            Row(
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Text(
                    text = temp,
                    fontSize = 63.sp,
                    color = Color.White,
                )

                Text(
                    text = unit,
                    fontSize = 30.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .padding(start = 8.dp)
                )

            }
            // Description
            Text(
                text = currentWeather.description,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(16.dp))

            // Weather Details
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                        .background(color = Color(0xff1E1F1C), shape = RoundedCornerShape(24.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WeatherDetailsSection(
                        R.drawable.humidity,
                        humidity,
                        stringResource(R.string.humidity),
                        stringResource(R.string.percentage_symbol)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .weight(1f)
                        .background(color = Color(0xff1E1F1C), shape = RoundedCornerShape(24.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WeatherDetailsSection(
                        R.drawable.pressure,
                        pressure,
                        stringResource(R.string.pressure),
                        stringResource(R.string.hpa)
                    )
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                        .background(
                            color = colorResource(R.color.grayish_green),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WeatherDetailsSection(
                        R.drawable.wind,
                        windSpeedValue,
                        stringResource(R.string.wind),
                        windSpeedUnit
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .weight(1f)
                        .background(
                            color = colorResource(R.color.grayish_green),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WeatherDetailsSection(
                        R.drawable._3d,
                        cloud,
                        stringResource(R.string.clouds),
                        stringResource(R.string.percentage_symbol)
                    )
                }
            }
        }
    }
}
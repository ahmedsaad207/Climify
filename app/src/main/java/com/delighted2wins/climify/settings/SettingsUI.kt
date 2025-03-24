package com.delighted2wins.climify.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delighted2wins.climify.utils.SP_NAME

@Composable
fun SettingsUI(showBottomNabBar: MutableState<Boolean>, onNavigateToMap: () -> Unit) {
    showBottomNabBar.value = true
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    val lang = sharedPreferences.getString("lang", "en") ?: "en"
    val temp = sharedPreferences.getString("temp", "metric")?: "metric"
    val location = sharedPreferences.getString("location", "map")?: "map"
    val windSpeed = sharedPreferences.getString("wind_speed", "meter_per_sec")?: "meter_per_sec"
    val selectedLanguage = remember { mutableStateOf(lang) }
    val selectedTempUnit = remember { mutableStateOf(temp) }
    val selectedLocation = remember { mutableStateOf(location) }
    val selectedWindSpeed = remember { mutableStateOf(windSpeed) }

    Column {
        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(text = "Language", fontSize = 24.sp)
            Row {
                RowOption(
                    label = "Arabic",
                    selectedOption = selectedLanguage,
                    value = "ar",
                    sharedPreferences,
                    "lang"
                )

                RowOption(
                    label = "English",
                    selectedOption = selectedLanguage,
                    value = "en",
                    sharedPreferences,
                    "lang"
                )

                // Option 3
                RowOption(
                    label = "Default",
                    selectedOption = selectedLanguage,
                    value = "Default",
                    sharedPreferences,
                    "lang"
                )
            }
        }

        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(text = "Temp Unit", fontSize = 24.sp)
            Row {
                RowOption(
                    label = "Celsius",
                    selectedOption = selectedTempUnit,
                    value = "metric",
                    sharedPreferences,
                    "temp"
                )

                RowOption(
                    label = "Kelvin",
                    selectedOption = selectedTempUnit,
                    value = "standard",
                    sharedPreferences,
                    "temp"
                )

                // Option 3
                RowOption(
                    label = "Fahrenheit ",
                    selectedOption = selectedTempUnit,
                    value = "imperial",
                    sharedPreferences,
                    "temp"
                )
            }
        }

        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(text = "Location", fontSize = 24.sp)
            Row {
                RowOption(
                    label = "Gps",
                    selectedOption = selectedLocation,
                    value = "gps",
                    sharedPreferences,
                    "location"
                )

                RowOption(
                    label = "Map",
                    selectedOption = selectedLocation,
                    value = "map",
                    sharedPreferences,
                    "location",
                    onNavigateToMap
                )
            }
        }

        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(text = "Wind Speed Unit", fontSize = 24.sp)
            Row {
                RowOption(
                    label = "meter/sec",
                    selectedOption = selectedWindSpeed,
                    value = "meter_per_sec",
                    sharedPreferences,
                    "wind_speed"
                )

                RowOption(
                    label = "mile/hour",
                    selectedOption = selectedWindSpeed,
                    value = "mile_per_mile",
                    sharedPreferences,
                    "wind_speed"
                )
            }
        }
    }


}

@Composable
fun RowOption(
    label: String,
    selectedOption: MutableState<String>,
    value: String,
    sharedPreferences: SharedPreferences,
    key: String,
    onNavigateToMap: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selectedOption.value == value,
            onClick = {
                selectedOption.value = value
                sharedPreferences.edit().putString(key, value).apply()
                if (value == "map") {
                    onNavigateToMap()
                }
            }
        )
        Text(text = label)
    }
}

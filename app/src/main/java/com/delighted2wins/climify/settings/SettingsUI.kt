package com.delighted2wins.climify.settings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.enums.Language
import com.delighted2wins.climify.enums.LocationSource
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.enums.WindSpeedUnit
import com.delighted2wins.climify.home.getRepo
import com.delighted2wins.climify.utils.Constants


@Composable
fun SettingsUI(showBottomNabBar: MutableState<Boolean>, onNavigateToMap: () -> Unit) {

    showBottomNabBar.value = true
    val context = LocalContext.current

    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(getRepo(context))
    )

    val sharedPreferences =
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    val lang = viewModel.getData(Constants.KEY_LANG) as Language
    val temp = viewModel.getData(Constants.KEY_TEMP_UNIT) as TempUnit
    val location: LocationSource = viewModel.getData(Constants.KEY_LOCATION_SOURCE)
    val windSpeed: WindSpeedUnit = viewModel.getData(Constants.KEY_WIND_SPEED_UNIT)

    viewModel.saveData(Pair("1", "1"))

    val selectedLanguage = remember { mutableStateOf(lang) }
    val selectedTempUnit = remember { mutableStateOf(temp) }
    val selectedLocation = remember { mutableStateOf(location) }
    val selectedWindSpeed = remember { mutableStateOf(windSpeed) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // language
        Column(
            modifier = Modifier
                .padding(start = 24.dp, top = 48.dp)
        ) {
            Text(text = "Language", fontSize = 24.sp, color = Color.White)
            Row {
                RowOption(
                    label = "عربى",
                    selected = selectedLanguage,
                    value = Language.AR,
                    onSelect = { viewModel.saveData(it) }
                )

                RowOption(
                    label = "English",
                    selected = selectedLanguage,
                    value = Language.EN,
                    onSelect = { viewModel.saveData(it) }
                )

                // Option 3
                RowOption(
                    label = "Default",
                    selected = selectedLanguage,
                    value = Language.DEFAULT,
                    onSelect = { viewModel.saveData(it) }
                )
            }
        }
        // temp unit
        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(text = "Temp Unit", fontSize = 24.sp, color = Color.White)
            Row {
                RowOption(
                    label = "Celsius",
                    selected = selectedTempUnit,
                    value = TempUnit.METRIC,
                    onSelect = { viewModel.saveData(it) }
                )

                RowOption(
                    label = "Kelvin",
                    selected = selectedTempUnit,
                    TempUnit.STANDARD,
                    onSelect = { viewModel.saveData(it) }
                )

                // Option 3
                RowOption(
                    label = "Fahrenheit ",
                    selected = selectedTempUnit,
                    TempUnit.IMPERIAL,
                    onSelect = { viewModel.saveData(it) }
                )
            }
        }


        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(text = "Select Your Location", fontSize = 24.sp, color = Color.White)
            Row {
                RowOption(
                    label = "Gps",
                    selected = selectedLocation,
                    value = LocationSource.GPS,
                    onSelect = { viewModel.saveData(it) }
                )

                RowOption(
                    label = "Map",
                    selected = selectedLocation,
                    value = LocationSource.MAP,
                    onSelect = { viewModel.saveData(it) }
                )
            }
        }

        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(text = "Wind Speed Unit", fontSize = 24.sp, color = Color.White)
            Row {
                RowOption(
                    label = "meter/sec",
                    selected = selectedWindSpeed,
                    value = WindSpeedUnit.STANDARD,
                    onSelect = { viewModel.saveData(it) }
                )

                RowOption(
                    label = "mile/hour",
                    selected = selectedWindSpeed,
                    value = WindSpeedUnit.IMPERIAL,
                    onSelect = { viewModel.saveData(it) }
                )
            }
        }

//        AppNotificationToggle()
    }
}

@Composable
fun <T> RowOption(
    label: String,
    selected: MutableState<T>,
    value: T,
    onNavigateToMap: () -> Unit = {},
    onSelect: (T) -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected.value == value,
            onClick = {
                selected.value = value
                onSelect(value)
//                if (value == "map") {
//                    onNavigateToMap()
//                }
            }
        )
        Text(text = label, color = Color.White)
    }
}

/*@Composable
fun AppNotificationToggle() {
    var isEnabled by remember { mutableStateOf(true) }

    // Language
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Language", fontSize = 24.sp, color = Color.White)
        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEECD97)) // Light background color
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToggleButton("Arabic", isEnabled) { isEnabled = true }
            ToggleButton("English", !isEnabled) { isEnabled = false }
            ToggleButton("Default", !isEnabled) { isEnabled = false }
        }
    }

    // Temp Unit
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Temp Unit", fontSize = 24.sp, color = Color.White)
        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEECD97)) // Light background color
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToggleButton("Celsius", isEnabled) { isEnabled = true }
            ToggleButton("Kelvin", !isEnabled) { isEnabled = false }
            ToggleButton("Fahrenheit", !isEnabled) { isEnabled = false }
        }
    }
}

@Composable
fun ToggleButton(text: String, selected: Boolean, onClick: () -> Unit) {

    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = if (selected) Color.Black else Color.Gray,
        modifier = Modifier
            .shadow(if (selected) 16.dp else 0.dp, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures { onClick() }
            }
    )

}*/

/*@Preview
@Composable
fun PreviewToggle() {
    AppNotificationToggle()
}*/


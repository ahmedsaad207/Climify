package com.delighted2wins.climify.settings

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.R
import com.delighted2wins.climify.enums.Language
import com.delighted2wins.climify.enums.LocationSource
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.enums.WindSpeedUnit
import com.delighted2wins.climify.home.getRepo
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.restartActivity


@Composable
fun SettingsUI(showBottomNabBar: MutableState<Boolean>, onNavigateToMap: () -> Unit) {

    showBottomNabBar.value = true
    val context = LocalContext.current

    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(getRepo(context))
    )

    val lang = viewModel.getData(Constants.KEY_LANG) as Language
    val temp = viewModel.getData(Constants.KEY_TEMP_UNIT) as TempUnit
    val location: LocationSource = viewModel.getData(Constants.KEY_LOCATION_SOURCE)
    val windSpeed: WindSpeedUnit = viewModel.getData(Constants.KEY_WIND_SPEED_UNIT)

    val selectedLanguage = remember { mutableStateOf(lang) }
    val selectedTempUnit = remember { mutableStateOf(temp) }
    val selectedLocation = remember { mutableStateOf(location) }
    val selectedWindSpeed = remember { mutableStateOf(windSpeed) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // language
        Column(
            modifier = Modifier.padding(start = 24.dp, top = 48.dp)
        ) {
            Text(text = stringResource(R.string.language), fontSize = 24.sp, color = Color.White)
            Row {
                RowOption(label = stringResource(R.string.arabic_lang),
                    selected = selectedLanguage,
                    value = Language.AR,
                    onSelect = {
                        viewModel.saveData(it)
                        context.restartActivity()
                    })

                RowOption(label = stringResource(R.string.english),
                    selected = selectedLanguage,
                    value = Language.EN,
                    onSelect = {
                        viewModel.saveData(it)
                        context.restartActivity()
                    })

                // Option 3
                RowOption(label = stringResource(R.string.default_lang),
                    selected = selectedLanguage,
                    value = Language.DEFAULT,
                    onSelect = {
                        viewModel.saveData(it)
                        context.restartActivity()
                    })
            }
        }
        // temp unit
        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(text = stringResource(R.string.temp_unit), fontSize = 24.sp, color = Color.White)
            Row {

                RowOption(label = stringResource(R.string.kelvin),
                    selected = selectedTempUnit,
                    TempUnit.STANDARD,
                    onSelect = {
                        viewModel.saveData(it)
                        viewModel.saveData(WindSpeedUnit.STANDARD)
                        selectedWindSpeed.value = WindSpeedUnit.STANDARD
                    })

                RowOption(label = stringResource(R.string.celsius),
                    selected = selectedTempUnit,
                    value = TempUnit.METRIC,
                    onSelect = { unit ->
                        viewModel.saveData(unit)
                        viewModel.saveData(WindSpeedUnit.STANDARD)
                        selectedWindSpeed.value = WindSpeedUnit.STANDARD
                    })
                // Option 3
                RowOption(label = stringResource(R.string.fahrenheit),
                    selected = selectedTempUnit,
                    TempUnit.IMPERIAL,
                    onSelect = {
                        viewModel.saveData(it)
                        viewModel.saveData(WindSpeedUnit.IMPERIAL)
                        selectedWindSpeed.value = WindSpeedUnit.IMPERIAL
                    })
            }
        }

        // Wind Speed Unit
        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(
                text = stringResource(R.string.wind_speed_unit),
                fontSize = 24.sp,
                color = Color.White
            )
            Row {
                RowOption(label = stringResource(R.string.meter_sec),
                    selected = selectedWindSpeed,
                    value = WindSpeedUnit.STANDARD,
                    onSelect = {
                        viewModel.saveData(it)
                        viewModel.saveData(TempUnit.METRIC)
                        selectedTempUnit.value = TempUnit.METRIC
                    })

                RowOption(label = stringResource(R.string.mile_hour),
                    selected = selectedWindSpeed,
                    value = WindSpeedUnit.IMPERIAL,
                    onSelect = {
                        viewModel.saveData(it)
                        viewModel.saveData(TempUnit.IMPERIAL)
                        selectedTempUnit.value = TempUnit.IMPERIAL
                    })
            }
        }

        // user location
        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(
                text = stringResource(R.string.select_your_location),
                fontSize = 24.sp,
                color = Color.White
            )
            Row {
                RowOption(label = stringResource(R.string.auto_detect_gps),
                    selected = selectedLocation,
                    value = LocationSource.GPS,
                    onSelect = { viewModel.saveData(it) })

                RowOption(label = stringResource(R.string.using_map),
                    selected = selectedLocation,
                    value = LocationSource.MAP,
                    onSelect = {
                        viewModel.saveData(it)
                        onNavigateToMap()
                    })
            }
        }

//        UnitsSettingsScreen()
    }
}

@Composable
fun <T> RowOption(
    label: String,
    selected: MutableState<T>,
    value: T,
    onSelect: (T) -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected.value == value, onClick = {
            selected.value = value
            onSelect(value)
        })
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
}*/

/*@Composable
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

// with click

/*@Composable
fun UnitsSettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
    ) {
        Text(
            text = "Units",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
        )

        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        SettingRow("Temperature", listOf("Celsius (°C)", "Fahrenheit (°F)"))
        SettingRow("Wind", listOf("Kilometers per hour (km/h)", "Miles per hour (mph)"))
        SettingRow("Air pressure", listOf("Hectopascals (hPa)", "Atmospheres (atm)"))
        SettingRow("Visibility", listOf("Kilometers (km)", "Miles (mi)"))

        Spacer(modifier = Modifier.height(16.dp))

        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        AboutWeatherRow()
    }
}

@Composable
fun SettingRow(label: String, options: List<String>) {
    var selectedValue by remember { mutableStateOf(options.first()) }
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedValue,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedValue = option
                            expanded = false
                        },
                        leadingIcon = {
                            if (selectedValue == option) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.Blue
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AboutWeatherRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { *//* Navigate or show info *//* }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "About Weather",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Navigate",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}*/




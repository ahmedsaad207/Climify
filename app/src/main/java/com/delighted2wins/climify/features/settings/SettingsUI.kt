package com.delighted2wins.climify.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.R
import com.delighted2wins.climify.enums.Language
import com.delighted2wins.climify.enums.LocationSource
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.enums.WindSpeedUnit
import com.delighted2wins.climify.features.home.getRepo
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
        // header
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.settings),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB0B0B0),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2A2B26),
                            Color(0xFF20211D)
                        )
                    ), shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 12.dp)
                .shadow(
                    4.dp, shape = RoundedCornerShape(12.dp)
                )
                .wrapContentSize(Alignment.Center)
        )

        // language
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF2A2B26), Color(0xFF1E1F1C))
                            ), shape = RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.language),
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
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
            }
        }

        // temp unit
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF2A2B26), Color(0xFF1E1F1C))
                            ), shape = RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp)
                        .fillMaxWidth()
                )
                {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.temp_unit),
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
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
            }
        }

        // wind speed unit
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF2A2B26), Color(0xFF1E1F1C))
                            ), shape = RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp)
                        .fillMaxWidth()
                )
                {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.wind_speed_unit),
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
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
            }
        }

        // location
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF2A2B26), Color(0xFF1E1F1C))
                            ), shape = RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp)
                        .fillMaxWidth()
                )
                {
                    Row {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.select_your_location),
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
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
            }
        }
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






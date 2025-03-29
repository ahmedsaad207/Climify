package com.delighted2wins.climify.locationselection

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.R
import com.delighted2wins.climify.enums.LocationSource
import com.delighted2wins.climify.home.components.LoadingIndicator
import com.delighted2wins.climify.home.getRepo
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.getCountryNameFromCode
import com.delighted2wins.climify.utils.getUserLocationUsingGps
import com.delighted2wins.climify.weatherdetails.BackButton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.compose.autocomplete.components.PlacesAutocompleteTextField
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LocationSelectionUI(
    showBottomNabBar: MutableState<Boolean>,
    isFavorite: Boolean,
    onNavigateToHome: () -> Unit
) {
    showBottomNabBar.value = false
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val placesClient = remember { Places.createClient(context.applicationContext) }
    val viewModel: LocationSelectionViewModel = viewModel(
        factory = LocationSelectionViewModelFactory(getRepo(context), placesClient)
    )

    var isMapReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            delay(500)
            isMapReady = true
        }
    }

    if (isMapReady) {
        ShowMap(
            isFavorite,
            viewModel,
            onNavigateToHome
        )
    } else {
        LoadingIndicator()
    }
}

@Composable
private fun ShowMap(
    isFavorite: Boolean,
    viewModel: LocationSelectionViewModel,
    onNavigate: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var predictions by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
    var savedLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val marker = remember { mutableStateOf(MarkerState(position = savedLocation)) }
    var input by remember { mutableStateOf("") }
    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(savedLocation, 5f)
    }

    val (lat, lon) = viewModel.getData<Pair<Double, Double>>("LOCATION")
    val userLocation = viewModel.getData<LocationSource>(Constants.KEY_LOCATION_SOURCE).value
    val cities = viewModel.predictions.collectAsStateWithLifecycle()
    val selectedLocation = viewModel.currentLocation.collectAsStateWithLifecycle()
    val selectedLatLng by viewModel.selectedLatLng.collectAsStateWithLifecycle()

    if (userLocation == LocationSource.MAP.value) {
        savedLocation = if (lat != 0.0 && lon != 0.0) {
            LatLng(lat, lon)
        } else {
            LatLng(31.252321, 29.992283)
        }
    } else { // TODO check permission
        context.getUserLocationUsingGps { latitude, longitude ->
            savedLocation = LatLng(latitude, longitude)
        }
    }

    LaunchedEffect(Unit) { viewModel.getLocationInfo(lat, lon) }
    LaunchedEffect(input) {
        if (input.isBlank()) {
            predictions = emptyList()
        }
    }
    LaunchedEffect(cities.value) {
        predictions = cities.value
    }
    LaunchedEffect(selectedLatLng) {
        if (selectedLatLng.latitude != 0.0 && selectedLatLng.longitude != 0.0) {
            marker.value = MarkerState(selectedLatLng)
            cameraPosition.move(CameraUpdateFactory.newLatLng(selectedLatLng))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPosition,
            onMapClick = {
                marker.value = MarkerState(it)
                viewModel.getLocationInfo(it.latitude, it.longitude)
            })
        {
            Marker(
                state = marker.value,
                title = selectedLocation.value.country ?: "",
                snippet = selectedLocation.value.state ?: ""
            )
        }


        Row(
            modifier = Modifier.padding(top = 32.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            )
            { BackButton { onNavigate() } }

            PlacesAutocompleteTextField(
                searchText = input,
                predictions = predictions.map { it.toPlaceDetails() },
                onQueryChanged = {
                    input = it
                    scope.launch {
                        viewModel.query.emit(it)
                    }
                },
                onSelected = { autocompletePlace: AutocompletePlace ->
                    input = ""
                    scope.launch {
                        try {
                            viewModel.getLocationByPlaceId(autocompletePlace.placeId)
                            selectedLocation.value.country =
                                autocompletePlace.secondaryText.toString()
                            selectedLocation.value.state = autocompletePlace.primaryText.toString()
                        } catch (e: Exception) {
                            Log.d("TAG", "LocationSelectionUI: ${e.message}")
                        }
                    }
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
                    .padding(end = 16.dp)
                    .background(
                        if (input.isBlank()) {
                            Color.Transparent
                        } else {
                            colorResource(R.color.very_light_lilac)
                        }, shape = RoundedCornerShape(8.dp)
                    )
            )
        }

        if (selectedLocation.value.lat != null && selectedLocation.value.lon != null) {
            val newLat = marker.value.position.latitude
            val newLon = marker.value.position.longitude
            val buttonText = if (isFavorite) {
                stringResource(R.string.add_to_favorite)
            } else {
                stringResource(R.string.select_location)
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = colorResource(R.color.blue_azure),
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = selectedLocation.value.country?.getCountryNameFromCode()
                                    ?: "",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = selectedLocation.value.state ?: "",
                                fontSize = 14.sp,
                                color = colorResource(R.color.ios_blue)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (!isFavorite) {
                                viewModel.saveData(Pair(newLat, newLon))
                            } else {
                                viewModel.insertWeather(marker.value.position)
                            }
                            onNavigate()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.vivid_red)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(text = buttonText, color = Color.White, fontSize = 16.sp)
                    }
                }
            }
            /*Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(color = Color.DarkGray, RoundedCornerShape(50.dp))
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedLocation.value.country?.getCountryNameFromCode() ?: "",
                    color = Color.White
                )
                Text(text = selectedLocation.value.state ?: "", color = Color.White)
                Button(onClick = {
                    if (!isFavorite) {
                        viewModel.saveData(Pair(newLat, newLon))
                    } else {
                        viewModel.insertWeather(marker.value.position)
                    }
                    onNavigate()
                }) {
                    Text(
                        text = if (isFavorite) {
                            stringResource(R.string.add_to_favorite)
                        } else {
                            stringResource(R.string.select_location)
                        }
                    )
                }

            }*/
        }
    }
}

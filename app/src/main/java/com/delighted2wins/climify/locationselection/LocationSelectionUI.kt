package com.delighted2wins.climify.locationselection

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.data.local.WeatherDatabase
import com.delighted2wins.climify.data.local.WeathersLocalDataSourceImpl
import com.delighted2wins.climify.data.remote.RetrofitClient
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSourceImpl
import com.delighted2wins.climify.data.repo.WeatherRepositoryImpl
import com.delighted2wins.climify.utils.SP_NAME
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.compose.autocomplete.components.PlacesAutocompleteTextField
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LocationSelectionUI(onNavigateToHome: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    val placesClient: PlacesClient = Places.createClient(context.applicationContext)
    val viewModel: LocationSelectionViewModel = viewModel(
        factory = LocationSelectionViewModelFactory(
            WeatherRepositoryImpl(
                WeatherRemoteDataSourceImpl(RetrofitClient.service),
                WeathersLocalDataSourceImpl(
                    WeatherDatabase.getInstance(context.applicationContext).getWeatherDao()
                )
            ),
            placesClient
        )
    )

    val currentLocation = viewModel.currentState.observeAsState()
    val cities = viewModel.predictions.collectAsStateWithLifecycle()
    // TODO set current location from shared preferences
    val savedLocation = LatLng(31.252321, 29.992283)
    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(savedLocation, 5f)
    }
    var input by remember { mutableStateOf("") }
    val marker = remember { mutableStateOf(MarkerState(position = savedLocation)) }
    var isMapLoaded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isMapLoaded) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPosition,
            onMapLoaded = { isMapLoaded = true },
            onMapClick = {
                marker.value = MarkerState(LatLng(it.latitude, it.longitude))
                viewModel.getStateInfoByLocation(it.latitude, it.longitude)
            }
        ) {
            Marker(
                state = marker.value,
                title = currentLocation.value?.country ?: "",
                snippet = currentLocation.value?.state ?: ""
            )

        }

        PlacesAutocompleteTextField(
            searchText = input,
            predictions = cities.value.map { it.toPlaceDetails() },
            onQueryChanged = {
                input = it

                if (it.isBlank()) {
                    viewModel.predictions.value = emptyList()
                } else {
                    scope.launch {
                        viewModel.query.emit(it)
                    }
                }
            },
            onSelected = { autocompletePlace: AutocompletePlace ->
                input = ""
                scope.launch {
                    try {
                        val city = cities.value.filter {
                            it.getFullText(null).toString()
                                .contains(autocompletePlace.primaryText) &&
                                    it.getFullText(null).toString()
                                        .contains(autocompletePlace.secondaryText)
                        }
                        val position: LatLng
                        if (city.isNotEmpty()) {
                            val location = viewModel.getLocationByQuery(
                                city[0].getFullText(null).toString()
                            )
                            position =
                                LatLng(location.lat ?: 0.0, location.lon ?: 0.0)
                        } else {
                            position = LatLng(0.0, 0.0)
                        }
                        marker.value = MarkerState(position)
                        withContext(Dispatchers.Main) {
                            cameraPosition.move(
                                CameraUpdateFactory.newLatLng(
                                    position
                                )
                            )
                        }
                        viewModel.getStateInfoByLocation(
                            position.latitude,
                            position.longitude
                        )
                    } catch (e: Exception) {
                        Log.d("TAG", "LocationSelectionUI: ${e.message}")
                    }
                    viewModel.predictions.value = emptyList()
                }
            },
        )

        Column(
            modifier = Modifier
                .wrapContentWidth()
                .background(color = Color.DarkGray, RoundedCornerShape(50.dp))
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = currentLocation.value?.country ?: "", color = Color.White)
            Text(text = currentLocation.value?.state ?: "", color = Color.White)
            Button(onClick = {
                viewModel.insertWeather(
                    LatLng(
                        currentLocation.value?.lat ?: 0.0,
                        currentLocation.value?.lon ?: 0.0
                    )
                )
                onNavigateToHome()
                val lat = currentLocation.value?.lat ?: 0.0
                val lon = currentLocation.value?.lon ?: 0.0

                sharedPreferences.edit()
                    .putString("lat", lat.toString())
                    .putString("lon", lon.toString())
                    .commit()
            }) {
                Text(text = "Select location")
            }

        }
    }
}
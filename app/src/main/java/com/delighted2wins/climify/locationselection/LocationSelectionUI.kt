package com.delighted2wins.climify.locationselection

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.data.remote.RetrofitClient
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSourceImpl
import com.delighted2wins.climify.data.repo.WeatherRepositoryImpl
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Preview
@Composable
fun LocationSelectionUI() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val placesClient: PlacesClient = Places.createClient(context.applicationContext)
    val viewModel: LocationSelectionViewModel = viewModel(
        factory = LocationSelectionViewModelFactory(
            WeatherRepositoryImpl(
                WeatherRemoteDataSourceImpl(RetrofitClient.service)
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

        Column {
            TextField(
                value = input,
                onValueChange = {
                    input = it
                    scope.launch {
                        viewModel.query.emit(it)
                    }
                },
                label = { Text("Search for a city") }
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)
            ) {
                items(cities.value.size) {
                    Text(
                        text = cities.value[it].getFullText(null).toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.White)
                            .clickable {
                                input = ""
                                scope.launch {
                                    try {

                                        val location = viewModel.getLocationByQuery(
                                            cities.value[it].getFullText(null).toString()
                                        )
                                        val position =
                                            LatLng(location.lat ?: 0.0, location.lon ?: 0.0)
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
                            }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp
                    )
                }
            }
        }

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
            Button(onClick = {}) {
                Text(text = "Select location")
            }

        }
    }
}
package com.delighted2wins.climify.locationselection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.data.remote.RetrofitClient
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSourceImpl
import com.delighted2wins.climify.data.repo.WeatherRepositoryImpl
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Preview
@Composable
fun LocationSelectionUI() {
    val viewModel: LocationSelectionViewModel = viewModel(
        factory = LocationSelectionViewModelFactory(
            WeatherRepositoryImpl(
                WeatherRemoteDataSourceImpl(RetrofitClient.service)
            )
        )
    )
    val currentState = viewModel.currentState.observeAsState()

    val egypt = LatLng(31.252321, 29.992283)
    val markerState = remember { mutableStateOf(MarkerState(position = egypt)) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(egypt, 5f)
    }
    var isMapLoaded by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoaded = true },
            onMapClick = {
                markerState.value = MarkerState(LatLng(it.latitude, it.longitude))
                viewModel.getStateNameFromLocation(it.latitude, it.longitude)
            }
        ) {
            Marker(
                state = markerState.value,
                title = currentState.value?.country ?: "",
                snippet = currentState.value?.state ?: ""
            )

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
            Text(text = currentState.value?.country ?: "", color = Color.White)
            Text(text = currentState.value?.state ?: "", color = Color.White)
            Button(onClick = {}) {
                Text(text = "Select location")
            }

        }


    }
}
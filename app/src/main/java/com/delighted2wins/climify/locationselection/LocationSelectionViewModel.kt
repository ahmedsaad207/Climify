package com.delighted2wins.climify.locationselection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.model.State
import com.delighted2wins.climify.utils.toCurrentWeather
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LocationSelectionViewModel(
    private val repository: WeatherRepository,
    private val placesClient: PlacesClient
) : ViewModel() {
    private val _currentState = MutableLiveData<State>()
    val currentState: LiveData<State> = _currentState

    val query = MutableSharedFlow<String>()

    var predictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())

    init {
        viewModelScope.launch {
            query.debounce(500.milliseconds).collect { searchQuery ->
                if (searchQuery.isNotBlank()) {

                    val request =
                        FindAutocompletePredictionsRequest
                            .builder()
                            .setQuery(searchQuery)
                            .setTypesFilter(listOf(PlaceTypes.CITIES))
                            .build()

                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { predictions.value = it.autocompletePredictions }
                        .addOnFailureListener {
                            Log.i("TAG", "LocationSelectionUI: FailureListener ")
                        }
                }

            }
        }
    }

    fun getStateInfoByLocation(
        lat: Double,
        lon: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getStateInfoByLocation(lat, lon)
            if (list.isNotEmpty()) {
                _currentState.postValue(list[0])
            }
        }
    }

    suspend fun getLocationByQuery(query: String): State {
        return repository.getLocationByQuery(query)[0]
    }

    fun insertWeather(latLng: LatLng) {
        viewModelScope.launch {
            try {
                val currentWeather =
                    repository.getCurrentWeather(latLng.latitude, latLng.longitude, "metric")
                        .toCurrentWeather()

                val isInserted = repository.insertWeather(currentWeather)

                if (isInserted > 0) Log.i("TAG", "insertWeather: currentWeather: inserted") //TODO
                else Log.i("TAG", "insertWeather: isInserted: inserted") // TODO

            } catch (e: Exception) {
                // TODO
            }

        }
    }

}
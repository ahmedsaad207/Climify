package com.delighted2wins.climify.locationselection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.domainmodel.LocationInfo
import com.delighted2wins.climify.enums.Language
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.toCurrentWeather
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LocationSelectionViewModel(
    private val repository: WeatherRepository,
    private val placesClient: PlacesClient
) : ViewModel() {

    private val _currentLocation = MutableStateFlow(LocationInfo())
    val currentLocation = _currentLocation.asStateFlow()

    private val _selectedLatLng = MutableStateFlow(LatLng(0.0, 0.0))
    val selectedLatLng = _selectedLatLng.asStateFlow()

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
                            Log.e("TAG", "LocationSelectionUI: FailureListener ${it.message}")
                        }
                }

            }
        }
    }

    fun getLocationInfo(
        lat: Double,
        lon: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getStateInfoByLocation(lat, lon)
            if (list.isNotEmpty()) {
                _currentLocation.emit(list[0])
            }
        }
    }

    suspend fun getLocationByQuery(query: String): LocationInfo {
        return repository.getLocationByQuery(query)[0]
    }

    fun insertWeather(latLng: LatLng) {
        val unit = getData<TempUnit>(Constants.KEY_TEMP_UNIT).value
        val lang = getData<Language>(Constants.KEY_LANG).value
        viewModelScope.launch {
            try {
                val currentWeather =
                    repository.getCurrentWeather(
                        latLng.latitude,
                        latLng.longitude,
                        unit,
                        lang
                    )
                        .map {
                            it.toCurrentWeather()
                        }
                        .onEach { it.unit = unit }
                        .first()

                repository.insertWeather(currentWeather)
            } catch (e: Exception) {
                Log.i("TAG", "insertWeather: Error insert weather: ${e.message}")
            }

        }
    }

    fun <T> getData(key: String): T {
        return repository.getData(key)
    }

    fun <T> saveData(value: T) {
        repository.saveData(value)
    }


    fun getLocationByPlaceId(placeId: String) {
        val placeFields = listOf(Place.Field.LOCATION)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()
        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                val place = it.place
                val newPosition = place.location ?: LatLng(0.0, 0.0)
                viewModelScope.launch { _selectedLatLng.emit(newPosition) }
            }
            .addOnFailureListener {
                Log.e("TAG", "Error fetching place details: ${it.message}")
            }
    }
}
package com.delighted2wins.climify.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.ForecastWeather
import com.delighted2wins.climify.domainmodel.Response
import com.delighted2wins.climify.mappers.toCurrentWeather
import com.delighted2wins.climify.mappers.toForecastWeather
import com.delighted2wins.climify.mappers.toForecastWeatherList
import com.delighted2wins.climify.utils.filterForecastToHoursAndDays
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(private val repository: WeatherRepository) : ViewModel() {

    init {

    }

    override fun onCleared() {
        super.onCleared()
    }
    private val _uiState =
        MutableStateFlow<Response<Triple<CurrentWeather, List<ForecastWeather>, List<ForecastWeather>>>>(
            Response.Loading
        )
    val uiState = _uiState.asStateFlow()

    private val _weather =
        MutableStateFlow<CurrentWeather?>(null)
    val weather = _weather.asStateFlow()

    fun fetchWeatherData(
        localWeather: CurrentWeather,
        isOnline: Boolean = true,
        units: String = "metric",
        lang: String = "en"
    ) {
        if (isOnline) {
            fetchApiData(localWeather, units, lang)
        }
        else {
            fetchLocalData(localWeather)
        }
    }

    private fun fetchApiData(
        localWeather: CurrentWeather,
        units: String,
        lang: String
    ) {
        viewModelScope.launch {
            try {
                val currentWeatherDeferred = async {
                    repository.getCurrentWeather(
                        localWeather.lat,
                        localWeather.long,
                        units,
                        lang
                    )
                        .catch { e ->
                            _uiState.value = Response.Failure(e.message.toString())
                        }
                        .map {
                            it.toCurrentWeather()
                        }
                        .firstOrNull()
                }

                val upcomingForecastDeferred = async {
                    repository.getUpcomingForecast(localWeather.lat, localWeather.long, units)
                        .catch { e ->
                            _uiState.value = Response.Failure(e.message.toString())
                        }
                        .map {
                            it.toForecastWeatherList(units)
                        }
                        .firstOrNull()
                }

                val currentWeather = currentWeatherDeferred.await()
                val upcomingForecast = upcomingForecastDeferred.await()

                if (currentWeather != null && upcomingForecast != null) {
                    val (hours, days) = filterForecastToHoursAndDays(
                        currentWeather.toForecastWeather().apply { unit = units },
                        upcomingForecast
                    )
                    val data = Triple(currentWeather, hours, days)
                    currentWeather.id = localWeather.id
                    currentWeather.hoursForecast = hours
                    currentWeather.daysForecast = days
                    currentWeather.unit = units
                    repository.updateWeather(currentWeather)
                    _uiState.value = Response.Success(data)
                }
                else {
                    _uiState.value = Response.Failure("Failed to fetch data.")
                }
            } catch (e: Exception) {
                _uiState.value = Response.Failure(e.message.toString())
            }
        }
    }

    fun getWeatherById(id: Int) {
        viewModelScope.launch {
            repository.getWeatherById(id).collect {
                _weather.emit(it)
            }
        }
    }

    private fun fetchLocalData(data: CurrentWeather) = viewModelScope.launch {
        _uiState.emit(Response.Success(Triple(data, data.hoursForecast, data.daysForecast)))
    }

    fun <T> getData(key: String): T {
        return repository.getData(key)
    }
}
package com.delighted2wins.climify.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.ForecastWeather
import com.delighted2wins.climify.domainmodel.Response
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.mappers.toCurrentWeather
import com.delighted2wins.climify.mappers.toForecastWeather
import com.delighted2wins.climify.mappers.toForecastWeatherList
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.filterForecastToHoursAndDays
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState =
        MutableStateFlow<Response<Triple<CurrentWeather?, List<ForecastWeather>, List<ForecastWeather>>>>(
            Response.Loading
        )
    val uiState = _uiState.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        fetchLocalData()
    }

    fun fetchWeatherData(
        lat: Double,
        lon: Double,
        lang: String,
        isOnline: Boolean
    ) {
        val units = getData<TempUnit>(Constants.KEY_TEMP_UNIT).value

        if (isOnline) {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                try {
                    val currentWeatherDeferred = async {
                        repository.getCurrentWeather(lat, lon, units, lang)
                            .catch { e ->
                                fetchLocalData()
                            }
                            .map {
                                it.toCurrentWeather()
                            }
                            .onEach {
                                it.unit = units
                            }
                            .firstOrNull()
                    }

                    val upcomingForecastDeferred = async {
                        repository.getUpcomingForecast(lat, lon, units)
                            .catch { e ->
                                fetchLocalData()
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
                        _uiState.value = Response.Success(data)
                        currentWeather.id = 1
                        currentWeather.hoursForecast = hours
                        currentWeather.daysForecast = days
                        val inserted = repository.insertWeather(currentWeather)
                    }
                    else {
                        fetchLocalData()
                    }
                }
                catch (e: Exception) {
                    fetchLocalData()
                }
            }
        }
        else {
            fetchLocalData()
        }
    }

    private fun fetchLocalData() {
        viewModelScope.launch {
            try {
                repository.getCachedWeather()
                    .catch { _uiState.emit(Response.Failure("Connect to the internet to get last update!")) }
                    .collect {
                        val data =
                            Triple(it, it.hoursForecast, it.daysForecast)
                        _uiState.value = Response.Success(data)
                    }
            } catch (e: Exception) {
                _uiState.emit(Response.Failure("Connect to the internet to get last update!"))
            }
        }
    }

    fun <T> getData(key: String): T {
        return repository.getData(key)
    }

    fun <T> saveData(value: T) {
        repository.saveData(value)
    }
}
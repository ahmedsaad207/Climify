package com.delighted2wins.climify.features.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.domainmodel.Response
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.ForecastWeather
import com.delighted2wins.climify.enums.Language
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.filterForecastToHoursAndDays
import com.delighted2wins.climify.mappers.toCurrentWeather
import com.delighted2wins.climify.mappers.toForecastWeather
import com.delighted2wins.climify.mappers.toForecastWeatherList
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
        _uiState.value = Response.Failure(e.message.toString())
    }

    fun fetchWeatherData(
        lat: Double,
        lon: Double,
        isOnline: Boolean
    ) {
        val lang = getData<Language>(Constants.KEY_LANG).value
        val units = getData<TempUnit>(Constants.KEY_TEMP_UNIT).value

        if (isOnline) {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                try {
                    val currentWeatherDeferred = async {
                        repository.getCurrentWeather(lat, lon, units, lang)
                            .catch { e ->
                                _uiState.value = Response.Failure(e.message.toString())
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
                        _uiState.value = Response.Success(data)
                        currentWeather.id = 1
                        currentWeather.hoursForecast = hours
                        currentWeather.daysForecast = days
                        val inserted = repository.insertWeather(currentWeather)
                        Log.i("TAG", "fetchWeatherData: inserted= $inserted")
                    }
                    else {
                        _uiState.value = Response.Failure("Failed to fetch data.")
                    }
                }
                catch (e: Exception) {
                    _uiState.value = Response.Failure(e.message.toString())
                }
            }
        }
        else {
            viewModelScope.launch {
                repository.getCachedWeather()
                    .catch { _uiState.value = Response.Failure(it.message.toString()) }
                    .collect {
                        val data = Triple(it, emptyList<ForecastWeather>(), emptyList<ForecastWeather>())
                        _uiState.value = Response.Success(data)
                    }
            }
        }
    }

    fun <T> getData(key: String): T {
        return repository.getData(key)
    }
}
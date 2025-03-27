package com.delighted2wins.climify.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.Response
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.ForecastWeather
import com.delighted2wins.climify.utils.filterForecastToHoursAndDays
import com.delighted2wins.climify.utils.toCurrentWeather
import com.delighted2wins.climify.utils.toForecastWeather
import com.delighted2wins.climify.utils.toForecastWeatherList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState =
        MutableStateFlow<Response<Triple<CurrentWeather?, List<ForecastWeather>, List<ForecastWeather>>>>(
            Response.Loading
        )
    val uiState = _uiState.asStateFlow()

    fun fetchWeatherData(
        lat: Double = 10.7946,
        lon: Double = 106.5348,
        units: String = "metric",
        lang: String = "en"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentWeatherDeferred = async {
                    repository.getCurrentWeather(lat, lon, units, lang)
                        .catch { e ->
                            _uiState.value = Response.Failure(e.message.toString())
                        }
                        .map {
                            it.toCurrentWeather()
                        }
                        .firstOrNull()
                }

                val upcomingForecastDeferred = async {
                    repository.getUpcomingForecast(lat, lon, units)
                        .catch { e ->
                            _uiState.value = Response.Failure(e.message.toString())
                        }
                        .map {
                            it.toForecastWeatherList()
                        }
                        .firstOrNull()
                }

                val currentWeather = currentWeatherDeferred.await()
                val upcomingForecast = upcomingForecastDeferred.await()

                if (currentWeather != null && upcomingForecast != null) {
                    val (hours, days) = filterForecastToHoursAndDays(currentWeather.toForecastWeather(), upcomingForecast)
                    val data = Triple(currentWeather, hours, days)
                    _uiState.value = Response.Success(data)
                } else {
                    _uiState.value = Response.Failure("Failed to fetch data.")
                }
            } catch (e: Exception) {
                _uiState.value = Response.Failure(e.message.toString())
            }
        }
    }

    fun <T> getData(key: String): T {
        return repository.getData(key)
    }
}
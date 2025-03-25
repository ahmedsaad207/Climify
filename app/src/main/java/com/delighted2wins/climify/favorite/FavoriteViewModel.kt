package com.delighted2wins.climify.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.Response
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.domainmodel.CurrentWeather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState =
        MutableStateFlow<Response<List<CurrentWeather>>>(Response.Loading)
    val uiState = _uiState.asStateFlow()

    fun getFavoriteWeathers() {
        viewModelScope.launch {
            _uiState.value = Response.Loading
            try {
                repository.getFavoriteWeathers()
                    .catch { e ->
                        _uiState.emit(Response.Failure("Error: ${e.message}"))
                    }
                    .collect { weathers ->
                        _uiState.emit(Response.Success(weathers))
                    }
            } catch (e: Exception) {
                _uiState.emit(Response.Failure("Error: ${e.message}"))
            }

        }
    }

    fun deleteWeather(weather: CurrentWeather) = viewModelScope.launch {
        repository.deleteWeather(weather)

    }

    fun insertWeather(weather: CurrentWeather) = viewModelScope.launch {
        repository.insertWeather(weather)
    }
}
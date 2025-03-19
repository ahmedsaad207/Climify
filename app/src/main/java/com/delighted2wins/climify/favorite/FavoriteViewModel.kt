package com.delighted2wins.climify.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.model.CurrentWeather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weathers = MutableStateFlow<List<CurrentWeather>>(emptyList())
    val weathers: StateFlow<List<CurrentWeather>> = _weathers

    init {
        getFavoriteWeathers()
    }

    private fun getFavoriteWeathers() {
        viewModelScope.launch {
            try {
                repository.getFavoriteWeathers()
                    .catch { e ->
                        Log.i("TAG", "${e.message}")
                    } //TODO
                    .collect { weathers ->
                        _weathers.emit(weathers)
                    }
            } catch (e: Exception) {
                Log.i("TAG", "in catch: ${e.message}")
                // TODO
            }

        }
    }
}
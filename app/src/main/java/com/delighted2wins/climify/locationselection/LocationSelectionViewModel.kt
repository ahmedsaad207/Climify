package com.delighted2wins.climify.locationselection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.model.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationSelectionViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _currentState = MutableLiveData<State>()
    val currentState: LiveData<State> = _currentState

    fun getStateNameFromLocation(
        lat: Double,
        lon: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getStateNameFromLocation(lat, lon)
            if (list.isNotEmpty()) {
                _currentState.postValue(list[0])
            }
        }
    }
}
package com.delighted2wins.climify.settings

import androidx.lifecycle.ViewModel
import com.delighted2wins.climify.data.repo.WeatherRepository

class SettingsViewModel(private val repository: WeatherRepository) : ViewModel() {

    fun <T> getData(type: String): T {
        return repository.getData(type)
    }

    fun <T> saveData(value: T) {
        repository.saveData(value)
    }
}
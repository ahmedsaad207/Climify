package com.delighted2wins.climify

import androidx.lifecycle.ViewModel
import com.delighted2wins.climify.data.repo.WeatherRepository

class AppViewModel(private val repository: WeatherRepository) : ViewModel() {

    fun <T> getData(type: String): T {
        return repository.getData(type)
    }
}
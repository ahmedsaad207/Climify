package com.delighted2wins.climify.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.delighted2wins.climify.data.repo.WeatherRepository

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}
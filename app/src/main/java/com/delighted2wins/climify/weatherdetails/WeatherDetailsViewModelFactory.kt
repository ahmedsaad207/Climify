package com.delighted2wins.climify.weatherdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.home.HomeViewModel

class WeatherDetailsViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherDetailsViewModel(repository) as T
    }
}
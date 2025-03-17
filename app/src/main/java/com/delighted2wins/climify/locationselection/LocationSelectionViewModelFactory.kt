package com.delighted2wins.climify.locationselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.google.android.libraries.places.api.net.PlacesClient

@Suppress("UNCHECKED_CAST")
class LocationSelectionViewModelFactory(
    private val repository: WeatherRepository,
    private val placesClient: PlacesClient
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationSelectionViewModel(repository,placesClient) as T
    }
}
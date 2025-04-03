package com.delighted2wins.climify

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.domainmodel.Alarm
import kotlinx.coroutines.launch

class AppViewModel(private val repository: WeatherRepository) : ViewModel() {

    fun <T> getData(type: String): T {
        return repository.getData(type)
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val deleted = repository.deleteAlarm(alarm)
            Log.i("TAG", "deleted: $deleted")
        }
    }
}

class AppViewModelFactory(private val repository: WeatherRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppViewModel(repository) as T
    }
}
package com.delighted2wins.climify

import android.util.Log
import androidx.lifecycle.ViewModel
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
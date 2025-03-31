package com.delighted2wins.climify.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.Response
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.domainmodel.Alarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AlarmViewModel(private val repo: WeatherRepository) : ViewModel() {

    private val _uiState =
        MutableStateFlow<Response<List<Alarm>>>(Response.Loading)
    val uiState = _uiState.asStateFlow()

    fun insertAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val inserted = repo.insertAlarm(alarm)
        }
    }

    fun getAllAlarms() = viewModelScope.launch {
        try {
            repo.getAllAlarms()
                .catch { e -> _uiState.emit(Response.Failure("Error: ${e.message}")) }
                .map { list ->
                    list.filter { it.startDuration > System.currentTimeMillis() }
                }
                .collect {
                    _uiState.emit(Response.Success(it))
                }
        } catch (e: Exception) {
            _uiState.emit(Response.Failure("Error: ${e.message}"))
        }
    }


    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val deleted = repo.deleteAlarm(alarm)
        }
    }
}
package com.delighted2wins.climify.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.model.CurrentWeather
import com.delighted2wins.climify.model.ForecastWeather
import com.delighted2wins.climify.utils.toCurrentWeather
import com.delighted2wins.climify.utils.toForecastWeatherList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _currentWeatherLiveData = MutableLiveData<CurrentWeather>()
    val currentWeatherLiveData: LiveData<CurrentWeather> = _currentWeatherLiveData

    private val _hourlyForecastWeatherList = MutableLiveData<List<ForecastWeather>>()
    val hourlyForecastWeatherList: LiveData<List<ForecastWeather>> = _hourlyForecastWeatherList

    private val _upcomingDaysForecastWeatherList = MutableLiveData<List<ForecastWeather>>()
    val upcomingDaysForecastWeatherList: LiveData<List<ForecastWeather>> =
        _upcomingDaysForecastWeatherList

    private var weather: ForecastWeather? = null

    init {
        getCurrentWeather()
    }

    private fun getCurrentWeather(
        lat: Double = 10.7946,
        lon: Double = 106.5348,
        units: String = "metric"
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherResponse = repository.getCurrentWeather(lat, lon, units)
                Log.e("TAG", "cod: ${weatherResponse.cod}")
                if ((weatherResponse.cod ?: 0) == 200) {
                    val currentWeather = weatherResponse.toCurrentWeather()
                    Log.e("TAG", "currentWeather: $currentWeather")
                    _currentWeatherLiveData.postValue(currentWeather)

                        weather = ForecastWeather(
                            dateText = "",
                            time = "Now",
                            date = currentWeather.dt,
                            icon = currentWeather.icon,
                            temp = currentWeather.temp,
                            tempMin = currentWeather.tempMin,
                            tempMax = currentWeather.tempMax,
                            description = currentWeather.description
                        )
                    getUpcomingForecast()
                }
            } catch (e: Exception) {
                Log.e("TAG", "getCurrentWeather: ${e.message}")
            }
        }

    private fun getUpcomingForecast(
        lat: Double = 10.7946,
        lon: Double = 106.5348,
        units: String = "metric"
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val upcomingForecastResponse = repository.getUpcomingForecast(lat, lon, units)
                if ((upcomingForecastResponse.cod ?: 0) == 200) {
                    val forecastWeatherList = upcomingForecastResponse.toForecastWeatherList()
                    val hourlyList = mutableListOf<ForecastWeather>()
                    val daysList = mutableListOf<ForecastWeather>()

                    if (weather != null) {
                        hourlyList.add(weather!!)
                    }

                    val calendar = Calendar.getInstance()
                    val currentDate = calendar.time
                    val formattedCurrentDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate)

                    forecastWeatherList.forEach { weather ->
                        val dateAndTime = weather.dateText.split(" ")
                        if (dateAndTime.contains(formattedCurrentDate)) {
                            hourlyList.add(weather)
                        } else {
                            if (weather.dateText.substring(11, 16) == "12:00") {
                                daysList.add(weather)
                            }
                        }
                    }

                    _hourlyForecastWeatherList.postValue(hourlyList)
                    _upcomingDaysForecastWeatherList.postValue(daysList)

                }
            } catch (e: Exception) {
                Log.e("TAG", "getUpcomingForecast: ${e.message}")
            }
        }
}
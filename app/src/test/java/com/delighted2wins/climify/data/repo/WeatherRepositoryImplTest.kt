package com.delighted2wins.climify.data.repo

import com.delighted2wins.climify.data.local.db.AlarmsLocalDataSource
import com.delighted2wins.climify.data.local.db.IWeathersLocalDataSource
import com.delighted2wins.climify.data.local.preferences.PreferencesDataSource
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSource
import com.delighted2wins.climify.domainmodel.CurrentWeather
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {
    lateinit var repo: WeatherRepository
    lateinit var weatherLocal: IWeathersLocalDataSource
    lateinit var alarmLocal: AlarmsLocalDataSource
    lateinit var remote: WeatherRemoteDataSource
    lateinit var preferences: PreferencesDataSource
    var weathersLocalList = emptyList<CurrentWeather>().toMutableList()
     private var remoteList = UpcomingForecastResponse(
         cod = 200, message = "message", cnt = 40, forecastWeatherList = arrayListOf(), city = null
     )

    private var remoteListInvalid = UpcomingForecastResponse(
        cod = 0, message = "", cnt = 0, forecastWeatherList = arrayListOf(), city = null
    )


    @Before
    fun setup() {
        weatherLocal = FakeWeatherLocalDataSource(weathersLocalList)
        alarmLocal = mockk(relaxed = true)
        remote = mockk(relaxed = true)
        preferences = mockk(relaxed = true)
        repo = WeatherRepositoryImpl(remote, weatherLocal, alarmLocal, preferences)
        coEvery { remote.getUpcomingForecast(0.0,0.0, "metric") } returns flowOf(remoteList)
        coEvery { remote.getUpcomingForecast(0.0,0.0, "standard") } returns flowOf(remoteListInvalid)
    }

    @Test
    fun insertWeather_returnFlowWeatherOrNull() = runTest {
        // given
        val weather = CurrentWeather(
            id = 8735,
            city = "Tennessee",
            dt = 3058,
            dateText = "habitasse",
            timeText = "eius",
            icon = 5529,
            background = 9078,
            temp = "his",
            tempMin = 10.11,
            tempMax = 12.13,
            pressure = "vulputate",
            humidity = "alia",
            windSpeed = 14.15,
            cloud = "integer",
            lat = 16.17,
            long = 18.19,
            description = "quam",
            country = "United Arab Emirates",
            unit = "altera",
            hoursForecast = listOf(),
            daysForecast = listOf()
        )

        // then
        assertThat(repo.insertWeather(weather), `is` (1L))
        assertThat(repo.getWeatherById(weather.id).first().id, `is`(weather.id))
        assertTrue(repo.getFavoriteWeathers().first().isNotEmpty())
        assertTrue(repo.getWeatherById(200).toList().isEmpty())


        // if list is null
        weatherLocal = FakeWeatherLocalDataSource(null)
        repo = WeatherRepositoryImpl(remote, weatherLocal, alarmLocal, preferences)
        assertTrue(repo.insertWeather(weather) == 0L)
        assertTrue(repo.getFavoriteWeathers().first().isEmpty())
    }

    @Test
    fun getUpcomingForecast_assertValidForecastResponse() = runTest{

        // when
        val response = repo.getUpcomingForecast(0.0,0.0,"metric").first()
        val responseTwo = repo.getUpcomingForecast(0.0,0.0,"standard").first()

        // then
        assertThat(response.cod, `is`(200))
        assertThat(response.cnt, `is`(40))
        assertNull(response.city)
        assertThat(responseTwo.cod, not(200))
        assertThat(responseTwo.cnt, not(40))
        assertTrue(responseTwo.message.isNullOrBlank())
    }

}
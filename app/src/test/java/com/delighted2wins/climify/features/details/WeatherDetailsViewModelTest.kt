package com.delighted2wins.climify.features.details

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.delighted2wins.climify.data.model.City
import com.delighted2wins.climify.data.model.CurrentWeatherResponse
import com.delighted2wins.climify.data.model.Main
import com.delighted2wins.climify.data.model.NetworkWeather
import com.delighted2wins.climify.data.model.UpcomingForecastResponse
import com.delighted2wins.climify.data.repo.WeatherRepository
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.Response
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class WeatherDetailsViewModelTest {
    private lateinit var viewModel: WeatherDetailsViewModel
    private lateinit var repo: WeatherRepository
    private val dispatcher = StandardTestDispatcher()

    val weather =
        mockk<CurrentWeather>(relaxed = true).apply { every { id } returns 200; every { lat } returns 0.0; every { long } returns 0.0 }
    val networkWeather = mockk<NetworkWeather>(relaxed = true)

    val currentWeatherResponse = CurrentWeatherResponse(
        weather = arrayListOf(networkWeather),
        main = Main(),
        id = 1,
        name = "cairo",
        cod = 200
    )
    val forecastResponse = UpcomingForecastResponse(
        cod = 200,
        cnt = 40,
        city = City(),
        forecastWeatherList = arrayListOf()
    )


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        repo = mockk(relaxed = true)
        viewModel = WeatherDetailsViewModel(repo)
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun fetchWeatherData_fetchDataSuccessfully_assertUiStateSuccess() = runTest {
        // given
        coEvery { repo.getCurrentWeather(any(), any(), any(), any()) } returns flowOf(
            currentWeatherResponse
        )
        coEvery { repo.getUpcomingForecast(any(), any(), any()) } returns flowOf(
            forecastResponse
        )

        // when
        viewModel.fetchWeatherData(weather, true, )
//        dispatcher.scheduler.advanceUntilIdle()

        // then
        val result = viewModel.uiState.first { it is Response.Success }
        System.out.println(result)
        assertTrue(result is Response.Success)
    }

    @Test
    fun fetchWeatherData_assertUiStateLoading() = run {
        // when
        viewModel.fetchWeatherData(weather, true)

        // then
        assertTrue(viewModel.uiState.value is Response.Loading)
    }

    @Test
    fun fetchWeatherData_assertUiStateFailure() = runTest {
        // given
        coEvery { repo.getCurrentWeather(any(), any(), any(), any()) } returns flow {
            throw Exception("Failed to fetch data")
        }

        // when
        viewModel.fetchWeatherData(weather, true)
        val result = viewModel.uiState.first {
            it is Response.Failure
        }

        // then
        assertTrue(result is Response.Failure)
        val message = result as Response.Failure
        assertThat(message.error, `is`("Failed to fetch data"))
    }

    @Test
    fun fetchLocalData_assertOfflineMode() = runTest {

        // when
        viewModel.fetchWeatherData(weather, false)
        val result = viewModel.uiState.first {
            it is Response.Success
        }

        // then
        assertTrue(result is Response.Success)
        val (current, hours, days) = (result as Response.Success).data
        assertThat(current.id, `is`(200))
    }

    @Test
    fun getWeatherById_assertWeatherStateNotNull() = runTest {
        assertNull(viewModel.weather.value)
        coEvery { repo.getWeatherById(any()) } returns flow {
            emit(weather)
        }

        // when
        viewModel.getWeatherById(200)
        val result = viewModel.weather.first() {
            it is CurrentWeather
        }

        // then
        assertNotNull(viewModel.weather.value)
        assertThat(result?.id, `is`(weather.id))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
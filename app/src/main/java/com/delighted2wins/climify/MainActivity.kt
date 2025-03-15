package com.delighted2wins.climify

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.data.remote.RetrofitClient
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSourceImpl
import com.delighted2wins.climify.data.repo.WeatherRepositoryImpl
import com.delighted2wins.climify.home.HomeUi
import com.delighted2wins.climify.home.WeatherViewModel
import com.delighted2wins.climify.home.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold { innerPadding ->
                HomeUi(
                    innerPadding,
                    viewModel(factory = WeatherViewModelFactory(WeatherRepositoryImpl(WeatherRemoteDataSourceImpl(RetrofitClient.service))))
                )
            }

        }
    }
}





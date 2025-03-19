package com.delighted2wins.climify.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delighted2wins.climify.data.local.WeatherDatabase
import com.delighted2wins.climify.data.local.WeathersLocalDataSourceImpl
import com.delighted2wins.climify.data.remote.RetrofitClient
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSourceImpl
import com.delighted2wins.climify.data.repo.WeatherRepositoryImpl
import com.delighted2wins.climify.home.HomeViewModel
import com.delighted2wins.climify.home.HomeViewModelFactory

@Composable
fun FavoriteUI(
    onNavigateToWeatherDetails: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(
            WeatherRepositoryImpl(
                WeatherRemoteDataSourceImpl(RetrofitClient.service),
                WeathersLocalDataSourceImpl(
                    WeatherDatabase.getInstance(context.applicationContext).getWeatherDao()
                )
            )
        )
    )
    val weathers = viewModel.weathers.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),

    ) {
        item {
            Text(
                text = "Favorite",
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        items(weathers.value.size) {
            Text(
                text = weathers.value.get(it).city,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp)
                    .clickable {
                        onNavigateToWeatherDetails()
                    },
                fontSize = 24.sp,
                color = Color.Black
            )
        }
    }

}
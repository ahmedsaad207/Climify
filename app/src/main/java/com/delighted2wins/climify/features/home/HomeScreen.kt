package com.delighted2wins.climify.features.home

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.delighted2wins.climify.R
import com.delighted2wins.climify.domainmodel.Response
import com.delighted2wins.climify.data.local.db.AlarmsLocalDataSourceImpl
import com.delighted2wins.climify.data.local.db.WeatherDatabase
import com.delighted2wins.climify.data.local.db.WeathersLocalDataSourceImpl
import com.delighted2wins.climify.data.local.preferences.PreferencesDataSourceImpl
import com.delighted2wins.climify.data.remote.RetrofitClient
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSourceImpl
import com.delighted2wins.climify.data.repo.WeatherRepositoryImpl
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.enums.LocationSource
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.features.home.components.DisplayHomeData
import com.delighted2wins.climify.features.home.components.LoadingIndicator
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.NetworkManager
import com.delighted2wins.climify.utils.getUserLocationUsingGps
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@Composable
fun HomeUi(
    notificationWeather: CurrentWeather?,
    showBottomNabBar: MutableState<Boolean>,
    snackBarHostState: SnackbarHostState,
    onNavigateToLocationSelection: (Boolean) -> Unit
) {
    var flag = true
    showBottomNabBar.value = true
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var hasPermission by remember { mutableStateOf(false) }
    var latLng by remember { mutableStateOf<LatLng?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(getRepo(context))
    )
    val networkManager = NetworkManager(context)
    val isOnline by networkManager.observeNetworkChanges()
        .collectAsStateWithLifecycle(networkManager.isNetworkAvailable())

    LaunchedEffect(isOnline) {
        val message =
            if (isOnline) (if (flag) "" else "Your Internet connection has been restored.") else "You are currently offline."
        if (message.isNotBlank()) {
            flag = false
            scope.launch {
                snackBarHostState.currentSnackbarData?.dismiss()
                snackBarHostState.showSnackbar(
                    message,
                    null,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    val userLocation = viewModel.getData<LocationSource>(Constants.KEY_LOCATION_SOURCE)
    val unit = viewModel.getData<TempUnit>(Constants.KEY_TEMP_UNIT).value
    val (lat, lon) = viewModel.getData<Pair<Double, Double>>("")
    if (notificationWeather != null) {
        FetchData(
            notificationWeather.lat,
            notificationWeather.long,
            viewModel,
            isOnline,
            onNavigateToLocationSelection,
            unit
        )

    } else if (userLocation == LocationSource.MAP && lat != 0.0 && lon != 0.0) {
        FetchData(lat, lon, viewModel, isOnline, onNavigateToLocationSelection, unit)
    } else {
        LocationPermissionHandler {
            hasPermission = true
        }

        if (hasPermission) {
            context.getUserLocationUsingGps { gpsLat, gpsLlon ->
                latLng = LatLng(gpsLat, gpsLlon)
            }
        }
    }

    latLng?.let {
        FetchData(
            it.latitude,
            it.longitude,
            viewModel,
            isOnline,
            onNavigateToLocationSelection,
            unit
        )
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            val locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (locationEnabled && (hasFineLocationPermission || hasCoarseLocationPermission)) {
                hasPermission = true
            }
        }

    }
}

@Composable
private fun FetchData(
    lat: Double,
    lon: Double,
    viewModel: HomeViewModel,
    isOnline: Boolean,
    onNavigateToLocationSelection: (Boolean) -> Unit,
    unit: String
) {
    LaunchedEffect(Unit) {
        viewModel.fetchWeatherData(lat, lon, isOnline)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is Response.Loading -> LoadingIndicator()

        is Response.Success -> {
            val (currentWeather, forecastHours, forecastDays) = (uiState as Response.Success).data

            if (isOnline) {
                // data from request
                DisplayHomeData(
                    currentWeather!!,
                    onNavigateToLocationSelection,
                    forecastHours,
                    forecastDays,
                    appUnit = unit
                )
            } else {
                // local data
                DisplayHomeData(
                    currentWeather!!,
                    onNavigateToLocationSelection,
                    currentWeather.hoursForecast,
                    currentWeather.daysForecast,
                    appUnit = unit,
                    isOnline = false
                )
            }

        }

        is Response.Failure -> {
            val error = (uiState as Response.Failure).error
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error3))
            val progress by animateLottieCompositionAsState(
                composition = composition, iterations = 1
            )
            Column {
                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(R.color.deep_gray)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnimation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        composition = composition,
                        progress = { progress },
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = error,
                        fontSize = 18.sp,
                        color = Color(0xFF808080),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun getRepo(context: Context) = WeatherRepositoryImpl(
    WeatherRemoteDataSourceImpl(RetrofitClient.service),
    WeathersLocalDataSourceImpl(
        WeatherDatabase.getInstance(context.applicationContext).getWeatherDao()
    ),
    AlarmsLocalDataSourceImpl(
        WeatherDatabase.getInstance(context.applicationContext).getWeatherDao()
    ),
    PreferencesDataSourceImpl(
        context.getSharedPreferences(
            Constants.PREF_NAME,
            Context.MODE_PRIVATE
        )
    )
)

@Composable
fun LocationPermissionHandler(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    var showRationale by remember { mutableStateOf(false) }
    var hasPermission by remember { // false
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isGpsEnabled by remember {
        mutableStateOf(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    }
    var permanentlyDenied by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        hasPermission = granted

        if (granted) {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (isGpsEnabled) {
                onPermissionGranted()
            }
        } else {
            showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            permanentlyDenied = !showRationale
        }
    }

    DisposableEffect(Unit) {
        val gpsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (isGpsEnabled) {
                    onPermissionGranted()
                }
            }
        }
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(gpsReceiver, filter)

        onDispose {
            context.unregisterReceiver(gpsReceiver)
        }
    }

    if (hasPermission && isGpsEnabled) {
        onPermissionGranted()
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!hasPermission) {
                // Permission Request
                Button(onClick = {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }) {
                    // TODO request permission
                    Text("Request Location Permission")
                }

                if (showRationale) { // first deny
                    Text(
                        "Location permission is needed for weather updates.",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                if (permanentlyDenied) {
                    Text(
                        "Permission permanently denied. Enable it from Settings.",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }) {
                        Text("Open Settings")
                    }
                }
            } else if (!isGpsEnabled) {
                // GPS Enable Request
                Text(
                    "Location services are disabled. Please enable GPS.",
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }) {
                    Text("Enable GPS")
                }
            }
        }
    }
}

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
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
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
import com.delighted2wins.climify.data.local.db.AlarmsLocalDataSourceImpl
import com.delighted2wins.climify.data.local.db.WeatherDatabase
import com.delighted2wins.climify.data.local.db.WeathersLocalDataSourceImpl
import com.delighted2wins.climify.data.local.preferences.PreferencesDataSourceImpl
import com.delighted2wins.climify.data.remote.RetrofitClient
import com.delighted2wins.climify.data.remote.WeatherRemoteDataSourceImpl
import com.delighted2wins.climify.data.repo.WeatherRepositoryImpl
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.Response
import com.delighted2wins.climify.enums.Language
import com.delighted2wins.climify.enums.LocationSource
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.features.home.components.DisplayHomeData
import com.delighted2wins.climify.features.home.components.LoadingIndicator
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.LocationHelper
import com.delighted2wins.climify.utils.NetworkManager
import com.delighted2wins.climify.utils.checkIfLangFromAppOrSystem
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
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    var hasPermission by remember { mutableStateOf(false) }
    var showData by remember { mutableStateOf<Boolean?>(null) }
    var latLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val locationHelper = LocationHelper(context)
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(getRepo(context))
    )
    val networkManager = NetworkManager(context)
    val isOnline by networkManager.observeNetworkChanges()
        .collectAsStateWithLifecycle(networkManager.isNetworkAvailable())

    BackHandler {
        activity?.finish()
    }

    LaunchedEffect(isOnline) {
        val message =
            if (isOnline) (if (flag) "" else context.getString(R.string.your_internet_connection_has_been_restored)) else context.getString(
                R.string.you_are_currently_offline
            )
        if (message.isNotBlank()) {
            flag = false
            scope.launch {
                snackBarHostState.currentSnackbarData?.dismiss()
                snackBarHostState.showSnackbar(
                    message, null, duration = SnackbarDuration.Short
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

    }
    else if (userLocation == LocationSource.MAP && lat != 0.0 && lon != 0.0) {
        FetchData(lat, lon, viewModel, isOnline, onNavigateToLocationSelection, unit)
    }
    else {
        if (!hasPermission) LocationPermissionHandler {
            hasPermission = true
        }
        if (hasPermission) {
            locationHelper.getLocationCoordinates { location ->
                location?.let {
                    latLng = it
                    viewModel.saveData(Pair(latLng.latitude, latLng.longitude))
                    showData = true
                } ?: run {
                    Log.e("TAG", "Failed to retrieve location.")
                }
            }
        }
    }

  if (userLocation == LocationSource.GPS && hasLocationPermission(context) && hasEnabledLocationService(locationManager)) {
        LoadingIndicator()
    }

    showData?.let {
        FetchData(
            latLng.latitude,
            latLng.longitude,
            viewModel,
            isOnline,
            onNavigateToLocationSelection,
            unit
        )
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if (hasLocationPermission(context) && hasEnabledLocationService(locationManager)) hasPermission = true
            else if (hasLocationPermission(context)) hasPermission = false
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun FetchData(
    lat: Double,
    lon: Double,
    viewModel: HomeViewModel,
    isOnline: Boolean,
    onNavigateToLocationSelection: (Boolean) -> Unit,
    unit: String,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val lang = viewModel.getData<Language>(Constants.KEY_LANG)
        viewModel.fetchWeatherData(lat, lon, context.checkIfLangFromAppOrSystem(lang), isOnline)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is Response.Loading -> LoadingIndicator()

        is Response.Success -> {
            val (currentWeather, forecastHours, forecastDays) = (uiState as Response.Success).data

            // data from request
            DisplayHomeData(
                currentWeather!!,
                onNavigateToLocationSelection,
                forecastHours,
                forecastDays,
                appUnit = unit,
                isOnline = isOnline
            )
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
    WeatherRemoteDataSourceImpl(RetrofitClient.service), WeathersLocalDataSourceImpl(
        WeatherDatabase.getInstance(context.applicationContext).getWeatherDao()
    ), AlarmsLocalDataSourceImpl(
        WeatherDatabase.getInstance(context.applicationContext).getWeatherDao()
    ), PreferencesDataSourceImpl(
        context.getSharedPreferences(
            Constants.PREF_NAME, Context.MODE_PRIVATE
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
        val granted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        hasPermission = granted

        if (granted) {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (isGpsEnabled) {
                onPermissionGranted()
            }
        } else {
            showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
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
                if (!permanentlyDenied) {
                    Button(
                        onClick = {
                            requestPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.vivid_red)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(56.dp)
                    ) {
                        Text(stringResource(R.string.request_location_permission), fontSize = 18.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))
                if (showRationale) { // first deny
                    Text(
                        stringResource(R.string.location_permission_is_needed_for_weather_updates),
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                if (permanentlyDenied) {

                    Button(
                        onClick = {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.vivid_red)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.open_settings), fontSize = 18.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.permission_permanently_denied_enable_it_from_settings),
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else if (!isGpsEnabled) {
                // GPS Enable Request
                Text(
                    stringResource(R.string.location_services_are_disabled_please_enable_gps),
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
                Button(
                    onClick = {
                        requestLocationService(context)

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.vivid_red)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(56.dp)
                ) {
                    Text(stringResource(R.string.enable_gps))
                }
            }
        }
    }
}

private fun requestLocationService(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}

private fun hasEnabledLocationService(locationManager: LocationManager): Boolean {
    val locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    return locationEnabled
}

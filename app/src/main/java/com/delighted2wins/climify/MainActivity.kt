package com.delighted2wins.climify

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.delighted2wins.climify.domainmodel.Alarm
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.enums.Language
import com.delighted2wins.climify.features.home.getRepo
import com.delighted2wins.climify.navigation.Screen
import com.delighted2wins.climify.navigation.SetupNavHost
import com.delighted2wins.climify.service.WeatherUpdateService
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.checkIfLangFromAppOrSystem
import com.delighted2wins.climify.utils.updateAppLanguage
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel =
            ViewModelProvider(this, AppViewModelFactory(getRepo(this)))[AppViewModel::class.java]
        val lang = viewModel.getData(Constants.KEY_LANG) as Language //

        if (!Places.isInitialized()) {
            Places.initialize(this.applicationContext, BuildConfig.PlacesApiKey)
        }
        var notificationWeather: CurrentWeather? = null

        WindowCompat.setDecorFitsSystemWindows(window, false)

        updateAppLanguage(checkIfLangFromAppOrSystem(lang))

        intent?.let { intent ->
            val json = intent.getStringExtra(Constants.KEY_CURRENT_WEATHER)
            json?.let {
                val alarm =
                    Gson().fromJson(intent.getStringExtra(Constants.KEY_ALARM), Alarm::class.java)

                notificationWeather = Gson().fromJson(json, CurrentWeather::class.java)
                val stopServiceIntent =
                    Intent(applicationContext, WeatherUpdateService::class.java).apply {
                        action = "STOP_SERVICE"
                    }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(applicationContext, stopServiceIntent)
                } else {
                    applicationContext.startService(stopServiceIntent)
                }
            }
        }

        setContent {
            val showFloatingActionButton = remember { mutableStateOf(false) }
            val snackBarHostState = remember { SnackbarHostState() }
            val showBottomNabBar = remember { mutableStateOf(true) }
            val navController = rememberNavController()
            val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val fabIcon = remember { mutableStateOf(Icons.Default.Add) }
            val fabAction = remember { mutableStateOf({}) }

            Scaffold(
                bottomBar = {
                    if (showBottomNabBar.value) {
                        BottomAppBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 18.dp, vertical = 8.dp),
                            containerColor = Color.Transparent
                        ) {
                            BottomNavigationBar(
                                navController,
                                showFloatingActionButton,
                                selectedNavigationIndex,
                                currentBackStackEntry,
                                fabIcon
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (showFloatingActionButton.value) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFE2AA12),
                                            Color(0xFF95700B)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .shadow(8.dp, shape = CircleShape)
                                .clickable {
                                    when (navController.currentDestination?.route?.substringAfterLast(
                                        "."
                                    )
                                        ?: "") {
                                        Screen.Alarm::class.simpleName -> {
                                            fabAction.value.invoke()
                                        }

                                        Screen.Favorite::class.simpleName -> {
                                            navController.navigate(Screen.LocationSelection(true))
                                            showFloatingActionButton.value = false
                                        }
                                    }
                                }
                        ) {
                            Icon(
                                imageVector = fabIcon.value,
                                contentDescription = "",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                },
                snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
            ) { _ ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(R.color.deep_gray))

                ) {
                    SetupNavHost(
                        navController,
                        showFloatingActionButton,
                        showBottomNabBar,
                        snackBarHostState,
                        notificationWeather,
                        onSetAlarm = { action ->
                            fabAction.value = action
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    showFloatingActionButton: MutableState<Boolean>,
    selectedNavigationIndex: MutableIntState,
    currentBackStackEntry: NavBackStackEntry?,
    fabIcon: MutableState<ImageVector>
) {
    val navigationBottomItems = listOf(
        NavigationItem(Icons.Rounded.Home, Screen.Home),
        NavigationItem(Icons.Rounded.FavoriteBorder, Screen.Favorite),
        NavigationItem(Icons.Rounded.Alarm, Screen.Alarm),
        NavigationItem(Icons.Rounded.Settings, Screen.Settings)
    )

    LaunchedEffect(currentBackStackEntry) {
        val currentRoute = currentBackStackEntry?.destination?.route?.substringAfterLast(".")

        when (currentRoute) {
            Screen.Alarm::class.simpleName -> {
                fabIcon.value = Icons.Default.AlarmAdd
            }

            Screen.Favorite::class.simpleName -> {
                fabIcon.value = Icons.Default.LocationOn
            }
        }
        val matchedScreen = navigationBottomItems.firstOrNull {
            it.route::class.simpleName == currentRoute
        }
        matchedScreen?.let {
            selectedNavigationIndex.intValue = navigationBottomItems.indexOf(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorResource(R.color.grayish_green),
                        colorResource(R.color.deep_gray)
                    )
                )
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            navigationBottomItems.forEachIndexed { i, navItem ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            selectedNavigationIndex.intValue = i
                            showFloatingActionButton.value =
                                (navItem.route == Screen.Favorite) || (navItem.route == Screen.Alarm)

                            if (navItem.route == Screen.Home) {
                                navController.popBackStack(navItem.route, inclusive = false)
                            } else {
                                navController.navigate(navItem.route) {
                                    popUpTo(Screen.Home) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        }
                ) {
                    if (selectedNavigationIndex.intValue == i) {
                        Box(
                            modifier = Modifier
                                .size(55.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            colorResource(R.color.neutral_gray),
                                            colorResource(R.color.grayish_green)
                                        )
                                    ),
                                    shape = RoundedCornerShape(30.dp)
                                )
                                .offset(y = (-10).dp)
                        )
                    }

                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = if (selectedNavigationIndex.intValue == i) Color.White
                        else colorResource(R.color.neutral_gray)
                    )
                }
            }
        }
    }
}

data class NavigationItem(
    var icon: ImageVector,
    val route: Screen
)


package com.delighted2wins.climify.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.features.alarm.AlarmUI
import com.delighted2wins.climify.features.details.DetailsUI
import com.delighted2wins.climify.features.favorite.FavoriteUI
import com.delighted2wins.climify.features.home.HomeUi
import com.delighted2wins.climify.features.location.LocationSelectionUI
import com.delighted2wins.climify.features.settings.SettingsUI
import com.delighted2wins.climify.features.splash.SplashUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SetupNavHost(
    navController: NavHostController,
    showFloatingActionButton: MutableState<Boolean>,
    showBottomNabBar: MutableState<Boolean>,
    snackBarHostState: SnackbarHostState,
    notificationWeather: CurrentWeather?,
    onSetAlarm: (() -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash
    ) {
        composable<Screen.Home> {
            HomeUi(notificationWeather, showBottomNabBar, snackBarHostState) {
                navController.navigate(Screen.LocationSelection(it))
            }
        }

        composable<Screen.Favorite> {
            FavoriteUI(showBottomNabBar, snackBarHostState, showFloatingActionButton) {
                navController.navigate(Screen.Details(it))
            }
        }

        composable<Screen.Alarm> {
            AlarmUI(snackBarHostState, onSetAlarm)
        }

        composable<Screen.Splash> {
            SplashUI(showBottomNabBar) {
                scope.launch {
                    withContext(Dispatchers.Main) {
                        navController.navigate(Screen.Home)
                    }
                }
            }
        }

        composable<Screen.Settings> {
            SettingsUI(showBottomNabBar) { navController.navigate(Screen.LocationSelection(false)) }
        }

        composable<Screen.Details> {
            val id = it.toRoute<Screen.Details>().id
            DetailsUI(id, showBottomNabBar, showFloatingActionButton, snackBarHostState) {
                navController.navigateUp()
            }
        }

        composable<Screen.LocationSelection> {
            val isFavorite = it.toRoute<Screen.LocationSelection>().isFavorite
            LocationSelectionUI(showBottomNabBar, isFavorite) {
                if (isFavorite) {
                    navController.navigateUp()
                } else {
                    navController.navigate(Screen.Home)
                }
            }
        }

    }
}
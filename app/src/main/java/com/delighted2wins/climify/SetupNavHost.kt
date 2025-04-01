package com.delighted2wins.climify

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.delighted2wins.climify.alarm.AlarmUI
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.favorite.FavoriteUI
import com.delighted2wins.climify.home.HomeUi
import com.delighted2wins.climify.locationselection.LocationSelectionUI
import com.delighted2wins.climify.settings.SettingsUI
import com.delighted2wins.climify.weatherdetails.DetailsUI

@Composable
fun SetupNavHost(
    navController: NavHostController,
    showFloatingActionButton: MutableState<Boolean>,
    showBottomNabBar: MutableState<Boolean>,
    snackBarHostState: SnackbarHostState,
    notificationWeather: CurrentWeather?,
    onSetAlarm: (() -> Unit) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeUi(notificationWeather, showBottomNabBar,snackBarHostState) {
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
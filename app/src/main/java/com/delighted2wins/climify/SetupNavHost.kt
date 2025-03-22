package com.delighted2wins.climify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.delighted2wins.climify.favorite.FavoriteUI
import com.delighted2wins.climify.home.HomeUi
import com.delighted2wins.climify.locationselection.LocationSelectionUI
import com.delighted2wins.climify.settings.SettingsUI
import com.delighted2wins.climify.weatherdetails.DetailsUI

//    snackBarHostState: SnackbarHostState,
//    titleState: MutableState<String>

@Composable
fun SetupNavHost(
    navController: NavHostController,
    showFloatingActionButton: MutableState<Boolean>,
    showBottomNabBar: MutableState<Boolean>,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeUi(showBottomNabBar){
                navController.navigate(Screen.LocationSelection)
            }
        }

        composable<Screen.Favorite> {
            FavoriteUI(showBottomNabBar) {
                navController.navigate(Screen.Details)
            }
        }

        composable<Screen.Alarm> {

        }

        composable<Screen.Settings> {
            SettingsUI{navController.navigate(Screen.LocationSelection)}
        }

        composable<Screen.Details> {
            DetailsUI()
        }

        composable<Screen.LocationSelection> {
            LocationSelectionUI(showBottomNabBar) {
                navController.navigateUp()
                showFloatingActionButton.value = true
            }
        }

    }
}
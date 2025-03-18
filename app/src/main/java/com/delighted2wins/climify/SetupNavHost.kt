package com.delighted2wins.climify

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.delighted2wins.climify.home.HomeUi
import com.delighted2wins.climify.locationselection.LocationSelectionUI

//    snackBarHostState: SnackbarHostState,
//    titleState: MutableState<String>

@Composable
fun SetupNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeUi{navController.navigate(Screen.LocationSelection)}
        }

        composable<Screen.Favorite> {
            FavoriteUI()
        }

        composable<Screen.Alarm> {

        }

        composable<Screen.Settings> {

        }

        composable<Screen.LocationSelection> {
            LocationSelectionUI {navController.navigateUp()}
        }

    }
}
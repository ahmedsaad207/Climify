package com.delighted2wins.climify

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(this.applicationContext, "AIzaSyCaj10hgcwGaosoYRyv79ppLviFJ9eMNmM")
        }

        setContent {
            val showFloatingActionButton = remember { mutableStateOf(false) }
            val navController = rememberNavController()
            Scaffold(
                bottomBar = {
                    BottomAppBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        containerColor = Color.Transparent
                    ) {
                        BottomNavigationBar(navController, showFloatingActionButton)
                    }
                },
                floatingActionButton = {
                    if (showFloatingActionButton.value) {
                        FloatingActionButton(onClick = {
                            showFloatingActionButton.value = false
                            navController.navigate(Screen.LocationSelection)
                        }) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Add Item")
                        }
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    SetupNavHost(navController, showFloatingActionButton)
                }
            }

        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, showFloatingActionButton: MutableState<Boolean>) {
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }
    val navigationBottomItems = listOf(
        NavigationItem(Icons.Filled.Home, Screen.Home),
        NavigationItem(Icons.Filled.Favorite, Screen.Favorite),
        NavigationItem(Icons.Filled.Notifications, Screen.Alarm),
        NavigationItem(Icons.Filled.Settings, Screen.Settings)
    )

    Row(Modifier.zIndex(1f)) {
        navigationBottomItems.forEachIndexed { i, navItem ->
            /*NavigationBarItem(
                selected = selectedNavigationIndex.intValue == i,
                onClick = {
                    selectedNavigationIndex.intValue = i
                },
                icon = { Icon(painter = painterResource(icon), contentDescription = null) },
                label = { Text("Home") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )*/
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        }
                    ) {
                        selectedNavigationIndex.intValue = i
                        navController.navigate(navItem.route)
                        showFloatingActionButton.value = navItem.route == Screen.Favorite
                    },
                contentAlignment = Alignment.BottomCenter,
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (selectedNavigationIndex.intValue == i) Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(topEnd = 22.dp, topStart = 22.dp)
                        )
                        .height(48.dp)
                        .width(48.dp)
                ) {}

                Box(modifier = Modifier.padding(bottom = 4.dp)) {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (selectedNavigationIndex.intValue == i) Color.White else Color.Black
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





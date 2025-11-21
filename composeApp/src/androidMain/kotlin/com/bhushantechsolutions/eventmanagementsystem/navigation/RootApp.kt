package com.bhushantechsolutions.eventmanagementsystem.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bhushantechsolutions.eventmanagementsystem.ui.EventDetailsScreen
import com.bhushantechsolutions.eventmanagementsystem.ui.EventMapScreen
import com.bhushantechsolutions.eventmanagementsystem.ui.HomeScreen
import com.bhushantechsolutions.eventmanagementsystem.ui.NoInternetScreen
import com.bhushantechsolutions.eventmanagementsystem.utils.ConnectivityObserver
import com.bhushantechsolutions.eventmanagementsystem.utils.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    var lastScreen by rememberSaveable { mutableStateOf(Routes.HOME) }
    var lastScreenRefreshAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val connectivityObserver = remember { ConnectivityObserver(context) }

    val isConnected by connectivityObserver.isConnected.collectAsState()

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            lastScreen = navController.currentBackStackEntry?.destination?.route ?: Routes.HOME
            navController.navigate(Routes.NO_INTERNET) {
                popUpTo(0)
            }
        } else {
            if (navController.currentDestination?.route == Routes.NO_INTERNET) {
                navController.navigate(lastScreen) {
                    popUpTo(0)
                }
                lastScreenRefreshAction?.invoke() // 👈 Refresh screen

            }
        }
    }
    NavHost(navController, startDestination = Routes.HOME) {



        composable(Routes.HOME) {
            HomeScreen(
                onEventClick = { eventId ->
                    navController.navigate("eventDetails/$eventId")
                },
                goToEventMap = {
                    navController.navigate(Routes.EVENT_MAP)
                },
                onRefresh = { lastScreenRefreshAction = it }

            )
        }

        composable(
            route = Routes.EVENT_DETAILS,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailsScreen(
                onEventClick = {
                    navController.navigate(Routes.HOME)
                },
                eventId = eventId,
                onRefresh = {
                    lastScreenRefreshAction = it
                }
            )
        }
        composable(Routes.EVENT_MAP) {
            EventMapScreen(
                onEventClick = {
                    navController.navigate(Routes.HOME)
                },
                onRefresh = {
                    lastScreenRefreshAction = it
                }
            )
        }
        composable(Routes.NO_INTERNET) {
            NoInternetScreen(onRetry = {

            })
        }
    }

}


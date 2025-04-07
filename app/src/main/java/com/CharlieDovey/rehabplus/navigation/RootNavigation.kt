package com.charliedovey.rehabplus.navigation

// Import necessary libraries for Compose and Android Navigation.
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
// Project imports.
import com.charliedovey.rehabplus.viewmodel.UserViewModel

/**
 * RootNavigation is the top level layout for the app.
 * This file attaches the BottomNavigationBar to the bottom of the screen,
 * and renders the AppNavigationGraph for the screen content.
 */

// A function which scaffolds the bottom navigation bar to the app screen.
@Composable
fun RootNavigation(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    // Get the current screen being shown in the app.
    val currentRoute =
        navController.currentBackStackEntryFlow // Attain the currentBackStackEntryFlow.
            .collectAsState(initial = navController.currentBackStackEntry) // Convert the flow to a Compose Ui state object.
            .value?.destination?.route // Attain the route as a string.

    // The routes which allow the BottomNavigationBar to be displayed on the screen.
    val bottomBarDisplayRoutes = listOf("home", "program", "progress", "settings")


    Scaffold(  // Use Scaffolds fixed layout with the navigation bottom bar.
        bottomBar = {
            // If the current route is within the bottomBarDisplayRoutes list pass the BottomNavigationBar to the navController.
            if (currentRoute in bottomBarDisplayRoutes) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding -> // Apply padding so the navigation bar doesn't overlap screen content.
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavigationGraph(
                navController = navController,
                userViewModel = userViewModel
            )
        }
    }
}

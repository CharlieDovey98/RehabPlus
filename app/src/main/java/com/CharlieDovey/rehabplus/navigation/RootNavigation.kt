package com.charliedovey.rehabplus.navigation

// Import necessary libraries for Compose and Android Navigation.
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
// Project imports.
import com.charliedovey.rehabplus.model.*
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
    userViewModel: UserViewModel,
    program: Program,
    exerciseMap: Map<String, Exercise>
) {
    Scaffold(  // Use Scaffolds fixed layout with the navigation bottom bar.
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding -> // Apply padding so the navigation bar doesn't overlap screen content.
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavigationGraph(
                navController = navController,
                userViewModel = userViewModel,
                program = program,
                exerciseMap = exerciseMap
            )
        }
    }
}

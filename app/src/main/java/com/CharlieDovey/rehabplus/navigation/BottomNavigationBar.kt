package com.charliedovey.rehabplus.navigation

// Import necessary libraries for Compose, Coil and Android Navigation.
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage

/**
 * BottomNavigationBar defines the apps permanent bottom navigation bar.
 * This file builds navigation items based the BottomNavItem list.
 * When the user taps on an icon, it navigates to the appropriate screens route.
 */

// A function that displays the apps permanent bottom navigation bar with functionality.
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // Get the current navigation backstack to highlight the nav item of the screen the user is seeing.
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    NavigationBar {
        // Display each item and its label whilst highlighting the
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    // Load each icon from its Azure URL.
                    AsyncImage(
                        model = item.iconUrl,
                        contentDescription = item.label,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = { Text(item.label)  },
                selected = currentRoute == item.route, // Show the highlight around the icon if its currently selected.
                onClick = {
                    navController.navigate(item.route) { // Navigate to the route.
                        popUpTo(navController.graph.startDestinationId) { saveState = true } // Keep the screens save state and remove duplicate screens in the stack.
                        launchSingleTop = true // Don’t add multiple copies of the same destination to the stack.
                        restoreState = true // Restore the state of the screen when navigating back.
                    }
                }
            )
        }
    }
}

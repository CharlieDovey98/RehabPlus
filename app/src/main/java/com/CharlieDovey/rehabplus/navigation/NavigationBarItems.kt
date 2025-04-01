package com.charliedovey.rehabplus.navigation

/**
 * NavigationBarItems defines the items within the bottom navigation bar of the app screen.
 * This file defines the four navigation items used within the navigation bar.
 */

// BottomNavItem class that defines the properties of a navigation item.
data class BottomNavItem(
    val route: String, // The route that's used to navigate to the screen.
    val label: String, // The label displayed under the icon
    val iconUrl: String // The Azure hosted image for the icon.
)

// The complete list of items within the bottom navigation bar.
val bottomNavItems = listOf(
    BottomNavItem(
        route = "home",
        label = "Home",
        iconUrl = "https://rehabplusmedia.blob.core.windows.net/images/Dashboard.png"
    ),
    BottomNavItem(
        route = "program",
        label = "Program",
        iconUrl = "https://rehabplusmedia.blob.core.windows.net/images/Program.png"
    ),
    BottomNavItem(
        route = "progress",
        label = "Progress",
        iconUrl = "https://rehabplusmedia.blob.core.windows.net/images/Calendar.png"
    ),
    BottomNavItem(
        route = "settings",
        label = "Settings",
        iconUrl = "https://rehabplusmedia.blob.core.windows.net/images/Settings.png"
    )
)

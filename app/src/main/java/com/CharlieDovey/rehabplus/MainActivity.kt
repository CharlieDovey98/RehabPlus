package com.charliedovey.rehabplus

// Importing necessary libraries for Android.
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController // Import the navigation controller object to handle app screen navigation.
// Project imports
import com.charliedovey.rehabplus.ui.theme.RehabPlusTheme
import com.charliedovey.rehabplus.navigation.* // Import the navigation components.
import com.charliedovey.rehabplus.viewmodel.UserViewModel

/**
 * MainActivity is the entry point of the app.
 * It holds the user interface content shown when when the app is launched.
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthManager.init(applicationContext) {} // Initialises MSAL with the application context.

        enableEdgeToEdge() // Enables full use of the phone screen.
        setContent { // Set the main UI content of the app.
            val navigationController = rememberNavController() // Create the navigation controller.
            val userViewModel: UserViewModel = viewModel()

            RehabPlusTheme { // Apply the RehabPlus theme.
                RootNavigation( // Launch the navigation system with a dummy program.
                    navController = navigationController,
                    userViewModel = userViewModel
                )
            }
        }
    }
}

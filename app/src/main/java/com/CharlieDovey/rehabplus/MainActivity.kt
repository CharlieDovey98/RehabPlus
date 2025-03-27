package com.charliedovey.rehabplus

// Importing necessary classes from Android and Jetpack Compose.
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge


// Project imports
import com.charliedovey.rehabplus.ui.screens.LoginScreen
import com.charliedovey.rehabplus.ui.theme.RehabPlusTheme

/**
 * MainActivity is the entry point of the app.
 * It holds the user interface content shown when when the app is launched.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // Enables full use of the phone screen.
            setContent { // Set the main UI content of the app.
                RehabPlusTheme { // Apply the RehabPlus theme.
                    LoginScreen() // Show the LoginScreen as the app's first screen.
                }
            }
        AuthManager.init(applicationContext) { // Initialises MSAL with the application context.
        }
    }
}

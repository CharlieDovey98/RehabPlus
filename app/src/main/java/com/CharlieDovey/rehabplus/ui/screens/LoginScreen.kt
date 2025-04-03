package com.charliedovey.rehabplus.ui.screens

// Importing necessary libraries for Android and Jetpack Compose.
import android.app.Activity
// Toast non intrusive, temporary popup messages.
import android.widget.Toast

// Jetpack Compose UI elements
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
// Project imports.
import com.charliedovey.rehabplus.AuthManager // Import AuthManager object.
import com.charliedovey.rehabplus.model.User

/**
 * LoginScreen handles user log in using composable.
 * This file uses jetpack compose ui for the sign in screen, with toast feedback messages.
 */

// A @Composable function from Jetpack Compose UI.
@Composable
fun LoginScreen(onLoginSuccess: (User) -> Unit) { // Callback to run the function when login has been achieved.
    val context = LocalContext.current
    val activity = context as? Activity

    // This state variable tracks if we're in the middle of a sign-in attempt
    var isLoading by remember { mutableStateOf(false) }

    // Box ui component.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Welcome to RehabPlus", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            // Sign In button.
            Button(
                onClick = {
                    // If there is an activity in progress.
                    if (activity != null) {
                        isLoading = true // Disable button and show its loading.

                        // Call AuthManager to begin login process.
                        AuthManager.signIn(activity) { user ->
                            isLoading = false
                            // If sign in is successful.
                            if (user != null) {
                                // Show success message and username.
                                Toast.makeText(
                                    context,
                                    "Welcome, ${user.name}",
                                    Toast.LENGTH_LONG
                                ).show()

                                onLoginSuccess(user) // Pass user to NavGraph to show on the Homescreen.
                            } else {
                                // Else show failed message if sign in failed.
                                Toast.makeText(
                                    context,
                                    "Sign-in failed. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                },
                enabled = !isLoading // Disable the button if its loading.
            ) {
                Text(if (isLoading) "Signing in... Please wait" else "Sign In") // Change button text with loading state.
            }
        }
    }
}

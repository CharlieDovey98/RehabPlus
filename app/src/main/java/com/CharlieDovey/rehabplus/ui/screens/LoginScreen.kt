package com.charliedovey.rehabplus.ui.screens

// Importing necessary libraries for Android and Jetpack Compose.
import android.app.Activity
import android.widget.Toast // Import Toast, non intrusive, temporary popup messages.
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
// Project imports.
import com.charliedovey.rehabplus.AuthManager
import com.charliedovey.rehabplus.viewmodel.UserViewModel

/**
 * LoginScreen handles user log in using composable.
 * This file uses jetpack compose ui for the sign in screen, with toast feedback messages.
 */

// A @Composable function to hold the sign in screen for RehabPlus.
@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onLoginSuccess: () -> Unit
) { // Callback to run the function when login has been achieved.
    val context =
        LocalContext.current // Access the current Android Context inside this loginScreen Composable.
    val activity = context as? Activity

    // This state variable tracks if the application is the middle of a sign in attempt.
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

            // Show the RehabPlus logo.
            AsyncImage(
                model = "https://rehabplusmedia.blob.core.windows.net/images/RehabPlus.png",
                contentDescription = "RehabPlus Logo",
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxSize()
                    .padding(bottom = 64.dp)
            )
            Text(
                "Please sign up or sign in to continue...",
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(64.dp))

            // Sign In button.
            Button(
                onClick = {
                    // If there is an activity in progress.
                    if (activity != null) {
                        isLoading = true // Disable button and show its loading.

                        // Call AuthManager to begin login process.
                        AuthManager.signIn(activity, userViewModel) { user ->
                            isLoading = false
                            // If sign in is successful.
                            if (user != null) {
                                // Show success message and the users name.
                                Toast.makeText(
                                    context,
                                    "Welcome, ${user.name}",
                                    Toast.LENGTH_LONG
                                ).show()
                                onLoginSuccess() // Call the function to move to the Homescreen.
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
                Text(if (isLoading) "Redirecting... Please wait" else "Sign In to RehabPlus") // Change button text with loading state.
            }
        }
    }
}
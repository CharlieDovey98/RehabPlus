package com.charliedovey.rehabplus.ui.screens

// Import necessary libraries for Jetpack Compose.
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
// Project imports
import com.charliedovey.rehabplus.viewmodel.UserViewModel
import com.charliedovey.rehabplus.AuthManager
import androidx.navigation.NavHostController


/**
 * Settings screen for the Rehab Plus app.
 * Allows users to change language, units, theme, notification settings, and view account/app info.
 */

@Composable
fun SettingsScreen(userViewModel: UserViewModel, navController: NavHostController) {
    // User interface state variable toggles for autoplay, dark mode and reminders.
    var autoplayEnabled by remember { mutableStateOf(false) }
    var remindersEnabled by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    val user =
        userViewModel.currentUser.collectAsState().value // Attain the current user from the UserViewModel for user information and settings.
    val context = LocalContext.current // Attain the current Android Context.

    // Allow the app to scroll.
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))


        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Column(modifier = Modifier.padding(8.dp)) {

                SectionTitle("About RehebPlus:")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Rehab Plus is a digital physiotherapy app designed to assist cancer patients with guided exercise programs for a better wellbeing, enhancing patient recovery and rehabilitation.")
                Spacer(modifier = Modifier.height(12.dp))
                SettingRow(label = "App version", value = "v1.0")
                SettingRow(label = "Developer", value = "Charlie Dovey")

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                SectionTitle("Account Info:")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Signed in as", fontWeight = FontWeight.SemiBold)
                SettingRow(
                    label = "Full Name",
                    value = user?.name ?: "user"
                ) // Display the users name and email from the signed in user.
                SettingRow(
                    label = "Email", value = user?.email ?: "user@hotmail.com"
                )
                Button(
                    onClick = { // Sign the user out and return the user to the login screen.
                        Toast.makeText(context, "Signing out...", Toast.LENGTH_SHORT).show()
                        AuthManager.signOut() {
                            userViewModel.clearCurrentUser()  // Clear the viewmodel of the user data.
                            userViewModel.clearAssignedProgram() // Clear the user specific assigned program from the view model.
                            navController.navigate("login") { // Call the navigation controller to move to the login screen.
                                // Remove all screens from the backstack so the user cannot return into the app after signing out.
                                popUpTo(navController.graph.startDestinationId) { // TODO: move into the navigationgraph.
                                    inclusive = true // Remove the start destination from the stack.
                                }
                                launchSingleTop = true // Avoid multiple instances of login.
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Sign out", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                SectionTitle("My programs:")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Hip Strengthening program", fontWeight = FontWeight.SemiBold)
                SettingRow(
                    label = "Program Start:",
                    value = "10/01/2025"
                ) // TODO: Have these hardcoded values update from program object.
                SettingRow(label = "Program End:", value = "10/04/2025")
                SettingRow(label = "Last Updated:,", value = "15/02/2025")

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                SectionTitle("App Settings:")
                Spacer(modifier = Modifier.height(8.dp))
                SettingRow(label = "Language", value = "English (UK)")
                SettingRow(label = "Weight units", value = "Kilograms (kg)")
                Spacer(modifier = Modifier.height(8.dp))

                ToggleRow(
                    label = if (darkModeEnabled) "App theme: Dark Mode" else "App theme: Light Mode",
                    isEnabled = darkModeEnabled
                ) { darkModeEnabled = it }
                ToggleRow(label = "Auto play exercise videos", isEnabled = autoplayEnabled) {
                    autoplayEnabled = it
                }
                ToggleRow(label = "Exercise reminders", isEnabled = remindersEnabled) {
                    remindersEnabled = it
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                SectionTitle("Privacy & Data")
                Text(
                    "View our data handling policy and your rights under GDPR.",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {}, // TODO: add popup with privacy and GDPR policy.
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Privacy Policy")
                }
                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}

// Helper function for a individual setting. Includes a label and text.
@Composable
fun SettingRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

// Helper function for a changeable value through use of a toggle.
@Composable
fun ToggleRow(label: String, isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = isEnabled, onCheckedChange = onToggle)
    }
}

// Helper function for section title styling on the screen.
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

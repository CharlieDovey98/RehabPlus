package com.charliedovey.rehabplus.ui.screens

// Import necessary libraries for Jetpack Compose.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
// Project Imports.
import com.charliedovey.rehabplus.viewmodel.UserViewModel

@Composable
fun HomeScreen(userViewModel: UserViewModel, onProgramClick: () -> Unit) {
    val scrollState = rememberScrollState()
    // Update on state change to get the currently logged in user from the ViewModel using StateFlow.
    val user = userViewModel.currentUser.collectAsState().value
    val assignedProgram = userViewModel.assignedProgram.collectAsState().value

    val tips = listOf( // A list of tips to be displayed on the homeScreen to the user.
        "* Take small breaks between exercises.",
        "* Stay hydrated throughout the day.",
        "* Breathe deeply and move with control.",
        "* Make sure to get enough sleep.",
        "* Consistency is more important than intensity."
    )

    val dailyTip = remember { tips.random() } // Select a random tip from the tips list.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Greeting dashboard title.
        Text(
            text = "Hello ${user?.name ?: "User"}", // Greeting message with the user's name.
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Notifications dashboard card.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notification")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("* Your RehablPlus top tip:")
                Spacer(modifier = Modifier.height(8.dp))
                Text(dailyTip, style = MaterialTheme.typography.bodyLarge) // Display the RehablPLus top top.
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Key Information dashboard card.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Key information!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("* Why follow a program?", fontWeight = FontWeight.SemiBold)
                Text("Using RehapPlus is a step in the right direction. Your rehabilitation program is tailored to your needs, helping to ease your symptoms and aid your recovery.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("* Why program adherence matters!", fontWeight = FontWeight.SemiBold)
                Text("Skipping workouts slows your progress. Completing your exercises daily improves mobility and speeds up recovery time. Small efforts daily make a big difference!")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // User Programs dashboard card.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("My Program", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (assignedProgram != null) {
                    Button(
                        onClick = { onProgramClick() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(assignedProgram.name)
                    }

                } else {
                    Text("Please wait for your physiotherapist to assign a personalised exercise program.")
                }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Statistics dashboard card.
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {

                Column(modifier = Modifier.padding(12.dp)) {
                    Text("My Statistics", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = "DateRange")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("7 Day streak!", fontWeight = FontWeight.Bold)

                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(it)
                                Text("✔")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Badges dashboard card.
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {

                Column(modifier = Modifier.padding(12.dp)) {
                    Text("My Badges", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text("First workout")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text("7 Day Streak")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text("100% a program")
                }
            }
        }
    }

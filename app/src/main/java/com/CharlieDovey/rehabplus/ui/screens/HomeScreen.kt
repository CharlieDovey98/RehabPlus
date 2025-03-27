package com.charliedovey.rehabplus.ui.screens

// Import necessary libraries for Jetpack Compose.
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@Composable
fun HomeScreen(username: String = "User") {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 45.dp)
    ) {
        // Greeting dashboard title.
        Text(
            text = "Hello $username", // Greeting message with the user's name.
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
                Text("* Please fill out the questionnaire") // need checks for if the user has completed.
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("questionnaire image")
                }
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
                Text("* Why follow a program?")
                Spacer(modifier = Modifier.height(8.dp))
                Text("* Why program adherence matters!")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // User Programs dashboard card.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("My Programs", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Navigate to ACL rehabilitation program screen*/ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ACL rehabilitation program")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Navigate to Hip Strengthening program screen */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Hip Strengthening program")
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

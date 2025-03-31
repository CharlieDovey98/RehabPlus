package com.charliedovey.rehabplus.ui.screens

// Import necessary libraries for Jetpack Compose and ViewModel.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
// Project imports.
import com.charliedovey.rehabplus.viewmodel.UserViewModel

/**
 * UserListScreen handles and displays of a list of users within the app.
 * This file gets data from the UserViewModel and automatically updates the UI.
 */

// A composable function that displays the user list on the screen.
@Composable
fun UserListScreen(viewModel: UserViewModel = viewModel()) {
    val users by viewModel.users.collectAsState() // Get the users StateFlow from the ViewModel.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 45.dp)
    ) {
        // Program title.
        Text(
            "Users within RehabPlus",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        // A LazyColumn to display a scrollable list as there may be several users.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Iterate through the users list and display each user on the screen.
            items(users) { user ->
                Button(
                    onClick = {}, // TODO: implement functionality to assign a chosen program to user.
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = "${user.name} - ${user.email}")
                }
            }
        }
    }
}
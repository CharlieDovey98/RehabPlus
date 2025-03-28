package com.charliedovey.rehabplus.ui.screens

// Import necessary libraries for Jetpack Compose.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

// Import Java libraries for working with date and time.
import java.text.SimpleDateFormat
import java.util.*
import com.charliedovey.rehabplus.model.Exercise
import com.charliedovey.rehabplus.model.Program
import com.charliedovey.rehabplus.model.AssignedExercise


/**
 * ProgramScreen is the base screen for user programs and the exercises assigned.
 * This file displays the main screen for a physiotherapy program, showing the assigned exercises with the physiotherapists instructions.
 */

@Composable
fun ProgramScreen(program: Program, exerciseMap: Map<String, Exercise>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 45.dp)
    ) {
        // Program title.
        Text(
            text = program.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Current Date, to be displayed at the top of the page.
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left arrow, used to cycle back in time by day.
            IconButton(onClick = {
                // TODO: Navigate to the previous days date.
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day")
            }

            // Date text in the middle.
            Text(
                text = currentDate,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 30.dp)
            )

            // Right arrow, used to cycle forwards in time by day.
            IconButton(onClick = {
                // TODO: Navigate to the next days date.
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Day")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Physiotherapist instructions card.
        Text("Physiotherapist instructions:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Please complete the program twice a day")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exercise cards displayed in a scrollable column list.
        LazyColumn( // LazyColumn only renders the items that are visible on the screen, saving resources and memory.
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Loop through each AssignedExercise in the current program.
            items(program.assignedExercises) { assigned ->
                // Use the assigned.exerciseId to look up the full Exercise object using the map passed into the program screen.
                val exercise = exerciseMap[assigned.exerciseId]
                if (exercise != null) { // Check that the referenced exercise exists.
                    Card( // Create a card and present the exercises details on.
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { // Onclick function to open the detailed exercise view with video explanation.
                            // TODO: On click navigate to detailed exercise page.
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Exercise thumbnail.
                            Icon(
                                imageVector = Icons.Default.PlayArrow, // Using a play icon for the video instead of a thumbnail. Change to thumbnail in the future.
                                contentDescription = "View exercise",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(50.dp)
                            )

                            Spacer(modifier = Modifier.width(40.dp))

                            // Core exercise information.
                            Column(modifier = Modifier.weight(1f)) {
                                Text(exercise.name, fontWeight = FontWeight.Bold)
                                Text(exercise.shortDescription, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    buildString {
                                        append("Reps: ${assigned.reps}   Sets: ${assigned.sets}")
                                        if (assigned.holdTime > 0) {
                                            append("   Hold: ${assigned.holdTime}s")
                                        }
                                    },
                                    fontSize = 12.sp
                                )
                            }

                            // Exercise completion check.
                            if (assigned.isComplete) { // Add a tick when selected exercise has been completed.
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Complete",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Complete program button.
        Button(
            onClick = {
                // TODO: Submit feedback and mark program as complete.
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Complete all exercises and submit feedback")
        }
    }
}

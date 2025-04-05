package com.charliedovey.rehabplus.ui.screens

// Import necessary libraries for Jetpack Compose.
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Project imports.
import com.charliedovey.rehabplus.model.AssignedExercise
import com.charliedovey.rehabplus.model.Exercise
import com.charliedovey.rehabplus.ui.components.VideoPlayer

@Composable
fun ExerciseDetailScreen(
    programName: String,
    assigned: AssignedExercise,
    exercise: Exercise,
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Back button and Program Name.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                Text("Back", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(programName, fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exercise Title and Navigation Arrows.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Switch to the previous exercise */ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Exercise")
            }

            Text(exercise.name, fontSize = 20.sp, style = MaterialTheme.typography.titleLarge)

            IconButton(onClick = { /* TODO: Switch to the next exercise */ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Exercise")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Video player for the exercise.
        VideoPlayer(
            videoUrl = exercise.videoUrl,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reps, sets and hold time.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                buildString {
                    append("Reps: ${assigned.reps}   Sets: ${assigned.sets}")
                    if (assigned.holdTime > 0) {
                        append("   Hold: ${assigned.holdTime} seconds")
                    }
                },
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Full Description.
        Text("Exercise description:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(exercise.longDescription, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // Exercise completed button.
        Button(
            onClick = onCompleteClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Complete exercise")
        }
    }
}

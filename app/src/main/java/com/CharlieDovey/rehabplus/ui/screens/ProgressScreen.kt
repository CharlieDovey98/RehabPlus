package com.charliedovey.rehabplus.ui.screens

// Imports necessary libraries for Jetpack Compose.
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Imports needed for the MPAndroidChart bar graph.
import android.graphics.Color as AndroidColor
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
// Project imports.
import com.charliedovey.rehabplus.viewmodel.UserViewModel


/**
 * Progress screen for the Rehab Plus app.
 * This screen allows the user to view earned badges, track weekly program adherence through a bar graph,
 * and see a brief summary of their workout statistics.
 */

@Composable
fun ProgressScreen(userViewModel: UserViewModel) {
    var weekIndex by remember { mutableIntStateOf(0) }

    // Simulated weekly data – each list represents 7 days of completion percentage.
    val weeks = listOf(
        listOf(20, 30, 35, 50, 40, 20, 50)
        //listOf(40, 40, 55, 50, 50, 60, 60),
        //listOf(70, 80, 75, 80, 90, 100, 100)
    )

    val currentWeek = weeks[weekIndex] // Get the current weeks data based on the selected index.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {

        Text(
            text = "Your progress",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // User attained badges.
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Badges", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("7 Day Streak") // Temporary badges.
                    Text("100% a program")
                    Text("First workout")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly adherence with a bar chart and controls to cycle weeks in future iterations.
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Your Program Adherence:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // Control arrows which will navigate through weeks.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Go to previous week if not at the first week of stored data.
                    IconButton(onClick = { if (weekIndex > 0) weekIndex-- }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous week"
                        )
                    }

                    Text("Week:", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    // Go to next week if not at last week of data stored.
                    IconButton(onClick = { if (weekIndex < weeks.lastIndex) weekIndex++ }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next week"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Temporary date range shown above the bar chart.
                Text(
                    text = "30/12/2024 - 05/01/2025",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Render the weekly bar chart using the composable chart function.
                WeeklyAdherenceChart(currentWeek)

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Each bar represents the program completion percent for that day.",
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // General statistics for user progress.
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Your RehabPlus Summary:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Total Workouts Completed: 42")
                Spacer(modifier = Modifier.height(4.dp))
                Text("Longest Streak: 12 days")
                Spacer(modifier = Modifier.height(4.dp))
                Text("Adherence: 76%")
            }
        }
    }
}


// Function used to draw the MPAndroidChart BarChart.
@Composable
fun WeeklyAdherenceChart(values: List<Int>) {
    // Day labels.
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // Use AndroidView to embed the user adherence BarChart.
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false // Remove the chart description.
                setDrawGridBackground(false)
                setScaleEnabled(false)
                setPinchZoom(false)
                axisRight.isEnabled = false // Hide the right axis.

                // Map the weekly values into chart data.
                val entries = values.mapIndexed { index, value ->
                    BarEntry(index.toFloat(), value.toFloat())
                }

                // Create and configure the bar dataset.
                val dataSet = BarDataSet(entries, "Adherence").apply {
                    color = AndroidColor.rgb(33, 150, 243)
                    valueTextColor = AndroidColor.BLACK
                    valueTextSize = 12f
                }
                data = BarData(dataSet)

                // X-axis configuration with day names.
                xAxis.valueFormatter = IndexAxisValueFormatter(days)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.labelRotationAngle = -45f
                xAxis.granularity = 1f
                xAxis.setLabelCount(days.size)

                // Y-axis configuration with a range of 0 to 100%.
                axisLeft.axisMinimum = 0f
                axisLeft.axisMaximum = 100f
                legend.isEnabled = false
                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    )
}
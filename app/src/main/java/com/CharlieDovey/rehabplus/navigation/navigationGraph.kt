package com.charliedovey.rehabplus.navigation

// Import necessary libraries for Jetpack Compose and Android Navigation.
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
// Project imports.
import com.charliedovey.rehabplus.model.*
import com.charliedovey.rehabplus.ui.screens.*

/**
 * navigationGraph defines the navigation between screens of the app.
 * AppNavigationGraph holds the navigation graph for the Rehab Plus app using Jetpack Compose Navigation.
 */

@Composable
fun AppNavigationGraph(
    navController: NavHostController,
    program: Program,
    exerciseMap: Map<String, Exercise>
) {
    // NavHost is the container that manages navigation between Composable screens.
    // The start destination is set to program for testing.
    NavHost(navController = navController, startDestination = "login") {

        // Login screen route.
        composable("login") {
            LoginScreen(
                onLoginSuccess = { user ->
                    navController.navigate("home/${user?.name ?: "User"}") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Home screen route.
        composable("home/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: "User"
            HomeScreen(username = username)
        }

        // Program screen route.
        composable("program") {
            ProgramScreen(
                program = program,
                exerciseMap = exerciseMap,
                onExerciseClick = { assigned ->
                    // On click navigate to the exercise detail screen using the exercise ID.
                    navController.navigate("exercise/${assigned.exerciseId}")
                }
            )
        }

        // User list screen, temporary view of users from azure cosmos db.
        // TODO: change for physiotherapist program and exercise selecting.
        composable("userList") {
            UserListScreen()
        }

        // Exercise detail screen route.
        composable("exercise/{exerciseId}") { backStackEntry ->
            // Extract the exerciseId from the navigation arguments.
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: return@composable
            // Find the assigned exercise in the current program using the ID.
            val assigned = program.assignedExercises.find { it.exerciseId == exerciseId } ?: return@composable
            // Use the ID to get the full exercise object from the map.
            val exercise = exerciseMap[exerciseId] ?: return@composable

            // Show the ExerciseDetailScreen, passing the relevant data.
            ExerciseDetailScreen(
                programName = program.name,
                assigned = assigned,
                exercise = exercise,
                onBackClick = { navController.popBackStack() },
                onCompleteClick = {
                    assigned.isComplete = true
                    navController.popBackStack()
                }
            )
        }
    }
}

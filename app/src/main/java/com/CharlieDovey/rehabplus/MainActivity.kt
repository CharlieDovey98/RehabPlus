package com.charliedovey.rehabplus

// Importing necessary libraries for Android.
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController // Import the navigation controller object to handle app screen navigation.
// Project imports
import com.charliedovey.rehabplus.model.AssignedExercise
import com.charliedovey.rehabplus.model.Exercise
import com.charliedovey.rehabplus.model.Program
import com.charliedovey.rehabplus.ui.screens.LoginScreen // Commented out due to errors with MSAL.
import com.charliedovey.rehabplus.ui.screens.HomeScreen
import com.charliedovey.rehabplus.ui.screens.ProgramScreen
import com.charliedovey.rehabplus.ui.theme.RehabPlusTheme
import com.charliedovey.rehabplus.navigation.AppNavigationGraph // Import the navigation graph composable function.

/**
 * MainActivity is the entry point of the app.
 * It holds the user interface content shown when when the app is launched.
 */

val dbExerciseList = listOf(
    // Temporary list of example exercises the app might use. To be replaced with exercises stored in azure.
    Exercise(
        "1",
        "Heel slides",
        "Bend the knee, bringing your heel towards your buttocks.",
        "Lie on your back with your legs straight and together. Bend the symptomatic leg as far as you can, sliding your heel towards your buttocks, keeping the knee upright throughout this movement. slide the heel back down, reversing the movement until your leg is straight again.",
        "VideoURL"
    ),
    Exercise(
        "2",
        "Step up",
        "Step up onto a raised platform.",
        "Stand facing a platform. Place one leg up on the platform. Step up bringing your other leg onto the platform and then step back down, returning to your starting position. Make sure your knee travels forwards over your toes during the stepping motion.",
        "https://rehabplusmedia.blob.core.windows.net/videos/Step-Up-Exercise.mp4"
    ),
    Exercise(
        "3",
        "Sideways step",
        "Step sideways onto a raised platform.",
        "Stand up straight, side on to a platform. Place your closest leg to the platform onto the platform and step up. Push through your raised leg, and stand tall, hovering your other leg above the floor. Return your down onto the floor keeping you other leg on the platform. You may use a handrail or chair to hold onto for support if required.",
        "VideoURL"
    ),
    Exercise(
        "4",
        "Step up 2",
        "Step up onto a raised platform.",
        "Stand facing a platform. Place one leg up on the platform. Step up bringing your other leg onto the platform and then step back down, returning to your starting position. Make sure your knee travels forwards over your toes during the stepping motion.",
        "https://rehabplusmedia.blob.core.windows.net/videos/Step-Up-Exercise.mp4"
    ),
    Exercise(
        "5",
        "Step up 3",
        "Step up onto a raised platform.",
        "Stand facing a platform. Place one leg up on the platform. Step up bringing your other leg onto the platform and then step back down, returning to your starting position. Make sure your knee travels forwards over your toes during the stepping motion.",
        "https://rehabplusmedia.blob.core.windows.net/videos/Step-Up-Exercise.mp4"
    ),
    Exercise(
        "6",
        "Step up 4",
        "Step up onto a raised platform.",
        "Stand facing a platform. Place one leg up on the platform. Step up bringing your other leg onto the platform and then step back down, returning to your starting position. Make sure your knee travels forwards over your toes during the stepping motion.",
        "https://rehabplusmedia.blob.core.windows.net/videos/Step-Up-Exercise.mp4"
    ),
    Exercise(
        "7",
        "Heel slides 2",
        "Bend the knee, bringing your heel towards your buttocks.",
        "Lie on your back with your legs straight and together. Bend the symptomatic leg as far as you can, sliding your heel towards your buttocks, keeping the knee upright throughout this movement. slide the heel back down, reversing the movement until your leg is straight again.",
        "VideoURL"
))

val sampleProgram = Program(
    id = "p1",
    name = "ACL Recovery Plan",
    instructions = "Follow this plan twice a day",
    assignedExercises = listOf(
        AssignedExercise("1", 10, 3, 30, false),
        AssignedExercise("2", 12, 3, 3, true),
        AssignedExercise("3", 15, 4, 5, false),
        AssignedExercise("2", 12, 3, 0, true),
        AssignedExercise("2", 12, 3, 3, false),
        AssignedExercise("2", 12, 3, 5, false),

        )
)
val exerciseMapBuild = dbExerciseList.associateBy { it.id }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // Enables full use of the phone screen.
        setContent { // Set the main UI content of the app.
            val navigationController = rememberNavController() // Create the navigation controller.
            RehabPlusTheme { // Apply the RehabPlus theme.
                AppNavigationGraph( // Launch the navigation system with a dummy program.
                    navController = navigationController,
                    program = sampleProgram,
                    exerciseMap = exerciseMapBuild
                )
                //HomeScreen(username = "Charlie")
                //LoginScreen() // [errors] Show the LoginScreen as the app's first screen.
            }
        }
        AuthManager.init(applicationContext) { // Initialises MSAL with the application context.
        }
    }
}

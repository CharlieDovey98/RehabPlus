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
//import com.charliedovey.rehabplus.ui.screens.LoginScreen // Commented out due to errors with MSAL.
//import com.charliedovey.rehabplus.ui.screens.HomeScreen
//import com.charliedovey.rehabplus.ui.screens.ProgramScreen
import com.charliedovey.rehabplus.ui.theme.RehabPlusTheme
import com.charliedovey.rehabplus.navigation.AppNavigationGraph // Import the navigation graph composable function.
import com.charliedovey.rehabplus.ui.screens.UserListScreen

/**
 * MainActivity is the entry point of the app.
 * It holds the user interface content shown when when the app is launched.
 */

val dbExerciseList = listOf(
    // Temporary list of example exercises the app might use. To be replaced with exercises stored in azure.
    Exercise(
        "1",
        "Step up",
        "Step up onto a raised platform.",
        "Stand facing a platform. Place one leg up on the platform. Step up bringing your other leg onto the platform and then step back down, returning to your starting position. Make sure your knee travels forwards over your toes during the stepping motion.",
        "https://rehabplusmedia.blob.core.windows.net/videos/Step-Up.mp4",
        "https://rehabplusmedia.blob.core.windows.net/images/Step-Up.png"
    ),
    Exercise(
        "2",
        "Bridging",
        "Lift hips off the ground into a bridge position.",
        "Lie on your back with your knees bent and feet flat on the floor. Engage your glutes and lift your hips upwards, creating a straight line from your shoulders to your knees. Hold briefly, then slowly lower your hips back to the ground in a controlled motion.",
        "https://rehabplusmedia.blob.core.windows.net/videos/Bridging-Exercise.mp4",
"https://rehabplusmedia.blob.core.windows.net/images/Bridging-Exercise.png"
    ),
    Exercise(
        "3",
        "Wall Squat",
        "Slide down the wall into a squat position.",
        "Stand with your back against a wall and your feet shoulder-width apart, about a foot away from the wall. Slowly slide down until your knees are at a 90-degree angle, keeping your back flat against the wall. Hold the position briefly and then push back up to standing.",
        "https://rehabplusmedia.blob.core.windows.net/videos/Wall-Squats.mp4",
        "https://rehabplusmedia.blob.core.windows.net/images/Wall-Squats.png"
    ),
    Exercise(
        "4",
        "Hamstring Stretch",
        "Stretch the back of the thigh to improve flexibility.",
        "Sit on the floor with one leg extended straight and the other bent inwards. Keeping your back straight, lean forward from your hips toward your extended foot, reaching with your hands until you feel a stretch in the back of your thigh. Hold the stretch, then return upright.",
        "https://rehabplusmedia.blob.core.windows.net/videos/Hamstring-Stretch.mp4",
        "https://rehabplusmedia.blob.core.windows.net/images/Hamstring-Stretch.png"
    ),
    Exercise(
        "5",
        "Hip Flexor Stretch",
        "Stretch the front of your hip and thigh.",
        "Begin in a kneeling lunge position with one foot forward and the other knee on the ground. Shift your weight forward slightly, keeping your torso upright, until you feel a stretch in the front of your hip on the kneeling leg. Hold the stretch and switch sides.",
        "https://rehabplusmedia.blob.core.windows.net/videos/Hip-Flexor-Stretch.mp4",
        "https://rehabplusmedia.blob.core.windows.net/images/Hip-Flexor-Stretch.png"
    )
    ,
    Exercise(
        "6",
        "Knee to Chest Stretch",
        "Pull your knee to your chest to release tension.",
        "Lie on your back with both legs extended. Gently pull one knee toward your chest using your hands, keeping the other leg flat on the floor. Hold the stretch, then switch sides. Keep your back relaxed and avoid lifting your head or shoulders.",
        "https://rehabplusmedia.blob.core.windows.net/videos/Knee-to-Chest-Stretch.mp4",
        "https://rehabplusmedia.blob.core.windows.net/images/Knee-To-Chest-Stretch.png"
    )
)

val sampleProgram = Program(
    id = "p1",
    name = "ACL Recovery Plan",
    instructions = "Follow this plan twice a day",
    assignedExercises = listOf(
        AssignedExercise("1", 10, 3, 5, false),
        AssignedExercise("2", 12, 3, 5, false),
        AssignedExercise("3", 15, 4, 3, false),
        AssignedExercise("4", 12, 3, 12, false),
        AssignedExercise("5", 12, 3, 8, false),
        AssignedExercise("6", 12, 3, 8, false),

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

package com.charliedovey.rehabplus.ui.screens

// Import necessary libraries for Jetpack Compose.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
// Project Imports.
import com.charliedovey.rehabplus.viewmodel.UserViewModel
import com.charliedovey.rehabplus.data.RetrofitInstance
import com.charliedovey.rehabplus.model.Questionnaire
import com.charliedovey.rehabplus.ui.components.DropdownQuestion

/**
 * QuestionnaireScreen is displayed for users who have not yet completed
 * their personalised program questionnaire. It displays a list of predefined
 * questions grouped into categories using dropdown boxes and Regex expressions to prevent input errors.
 */

// A composable function to show the questionnaire screen.
@Composable
fun QuestionnaireScreen(
    userViewModel: UserViewModel,
    onSubmitClick: () -> Unit
) {
    val user = userViewModel.currentUser.collectAsState().value
    val scrollState = rememberScrollState() // Allow scrolling within the Compose screen.
    val context = LocalContext.current // Access the context to allow for Toast notifications.

    var name by remember {
        mutableStateOf(
            user?.name ?: ""
        )
    } // Attain the name from the userViewmodel.
    var dob by remember { mutableStateOf("") } // Define date of birth and allow for the ui to react as the variable changes.
    val isDobValid =
        ValidDateCheck(dob) // Define a boolean variable which is the result of a validation check on the date of birth.

    val dateOfSubmission = remember { // Define date of submission for the questionnaire.
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    // List of questions to be asked to the user.
    val questions = listOf(
        // Physical Activity section.
        "1. Did you have any trouble doing strenuous activities, like carrying a heavy bag?",
        "2. Did you have any trouble taking a long walk?",
        "3. Did you have any trouble taking a short walk?",
        "4. Did you need to stay in bed or a chair during the day?",
        "5. Did you need to take frequent rests during the day?",
        // Limitations and Fatigue section.
        "6. Were you limited in pursuing your hobbies or other leisure time activities?",
        "7. Were you limited in doing either your work or other daily activities?",
        "8. Were you short of breath?",
        "9. Have you felt weak?",
        "10. Had the need to rest?",
        "11. Have you felt exhausted?",
        "12. Have you felt slowed down?",
        "13. Had tiredness interfere with your daily activities?",
        // Pain and Discomfort section.
        "14. Have you had pain within your body?",
        "15. Did pain interfere with your daily activities?",
        "16. Did you have pain in your affected area?",
        "17. Did you have swelling in your affected area?",
        "18. Was it difficult to raise or move your affected area?",
        "19. Have you had pain in affected area?",
        // Sleep and Recovery section.
        "20. Have you had trouble sleeping?",
        "21. Did you feel sleepy during the day?",
        // Overall Well-being section.
        "22. How would you rate your overall health (1–10, Very poor-Excellent)",
        "23. How would you rate your overall quality of life (1–10, Very poor-Excellent)"
    )

    // Define the responses using mutable list with each question as null to start with.
    val responses =
        remember { mutableStateListOf<String?>().apply { repeat(questions.size) { add(null) } } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text("Health Questionnaire", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Please fill in your details:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        // User name text input field.
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Date of birth text input field.
        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text("Date of Birth (DD/MM/YYYY)") },
            modifier = Modifier.fillMaxWidth(),
            isError = !isDobValid && dob.isNotBlank() // Display as an error if the requirements are true.
        )

        // Display an error message on the UI for the user.
        if (!isDobValid && dob.isNotBlank()) {
            Text(
                text = "Please enter DOB in DD/MM/YYYY format",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Questionnaire sections covered.
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "The following sections will address:\n" +
                    "- Physical Activity and Mobility\n" +
                    "- Limitations and Fatigue\n" +
                    "- Pain and Discomfort\n" +
                    "- Sleep and Recovery\n" +
                    "- Overall Well-being", style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        //
        Text(
            "Please fill in the form using the numbers associated to the condition: \n" +
                    "1 – Not at all.\n2 – A little.\n3 – Quite a bit.\n4 – Very much.",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Please answer the following questions\nIn the past 2 weeks:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        questions.forEachIndexed { index, question ->
            DropdownQuestion( // For each question, apply a dropdown box for he user to select an answer.
                index = index,
                question = question,
                responses = responses,
                // For all questions except the last two have options 1 to 4.
                // else the options expand to 1 - 10.
                options = if (index < 21)
                    listOf(
                        "1",
                        "2",
                        "3",
                        "4"
                    ) else (1..10).map { it.toString() }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Define a boolean to show if the form has been entered correctly.
        val isFormValid = name.isNotBlank() &&
                ValidDateCheck(dob) &&
                responses.none { it.isNullOrBlank() } // No responses are null or blank.

        // A button to configure the questionnaire and submit it.
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try { // A try catch to build the questionnaire and submit it.
                        // Map the questions to their answers converting the answers to integers.
                        val mappedResponses = questions.mapIndexed { index, q ->
                            q to (responses[index]?.toIntOrNull() ?: 0)
                        }.toMap()

                        val questionnaire = Questionnaire( // Define the questionnaire object using the data collected.
                            userEmail = user!!.email,
                            userName = name,
                            userDateOfBirth = dob,
                            dateOfSubmission = dateOfSubmission,
                            responses = mappedResponses
                        )

                        // Call the retrofit API to submit the questionnaire.
                        val response = RetrofitInstance.api.submitQuestionnaire(questionnaire)
                        if (response.isSuccessful) { // Display a toast notification message of the success.
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Questionnaire submitted successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                                userViewModel.markQuestionnaireComplete() // Mark the user as completed the questionnaire in the view model.
                                // Call the API to update the user in the Cosmos DB.
                                val markResponse = RetrofitInstance.api.markQuestionnaireComplete(
                                    mapOf("email" to (user.email))
                                )
                                if (!markResponse.isSuccessful) { // Log an error if the API unsuccessfully edited the user in Cosmos DB.
                                    Log.e(
                                        "Questionnaire",
                                        "Failed to mark questionnaire as complete for the user in CosmosDB."
                                    )
                                }

                                onSubmitClick()

                            }
                        } else {
                            Log.e("Questionnaire", "Error submitting the questionnaire  to Cosmos DB: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.e("Questionnaire", "Error submitting the questionnaire: ${e.message}")
                    }
                }
            },
            enabled = isFormValid, // The submit button is enabled if the form is valid.
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Questionnaire")
        }
    }
}

// A helper function to check a date string against a regex, returning a boolean.
fun ValidDateCheck(date: String): Boolean {
    val regex = Regex("^\\d{2}/\\d{2}/\\d{4}$")
    return regex.matches(date)
}

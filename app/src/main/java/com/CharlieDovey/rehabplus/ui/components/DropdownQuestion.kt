package com.charliedovey.rehabplus.ui.components

// Compose essentials
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Icons and dropdown
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

/**
 * DropdownQuestion is a helper file that displays a single dropdown field for a questionnaire item.
 * It uses a list of strings as options and stores the selected option into a mutable list.
 */

// A composable function called through the questionnaireScreen to show an answer box with dropdown options to select.
@Composable
fun DropdownQuestion(
    index: Int,
    question: String,
    responses: MutableList<String?>,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = question, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))

        Box { // The text box showing the users answer or empty before selection.
            OutlinedTextField(
                value = responses[index] ?: "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { if (responses[index].isNullOrBlank()) {
                    Text("Select an answer") // Show if the user hasn't selected anything yet.
                } },
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown icon")
                    }
                }
            )

            // The dropdown menu options for the user to select.
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option -> // For each response option display and enable the ability to select it as an answer.
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            responses[index] = option
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

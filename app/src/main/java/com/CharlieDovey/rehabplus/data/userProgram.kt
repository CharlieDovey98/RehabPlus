package com.charliedovey.rehabplus.data

// Android Log library for debugging output.
import android.util.Log
// Project model imports.
import com.charliedovey.rehabplus.model.Program
import com.charliedovey.rehabplus.model.User
import com.charliedovey.rehabplus.model.AssignedExercise
import com.charliedovey.rehabplus.model.Exercise

/**
 * userProgram is a helper object used to fetch a user program and its linked exercises from Azure
 * This file holds the function fetchFullAssignedProgram which returns a verified user program
 * and a map of all exercises in the database for fast lookup and displaying.
 */

object userProgram {

    // Function fetchFullAssignedProgram which uses Retrofit to attain and verify the users program and exercises from the database.
    suspend fun fetchFullAssignedProgram(user: User): Pair<Program, Map<String, Exercise>>? {
        var result: Pair<Program, Map<String, Exercise>>? = null // Declare the result variable.
        try {

            val programId =
                user.assignedProgramId // Attain the users assignedProgramId from the user.
            val program =
                RetrofitInstance.api.getProgramById(programId)  // Attain the users program details from the database using the programId.
            val allExercises =
                RetrofitInstance.api.getExercises() // Attain all exercises from the database.

            // Create a new list using the users assigned exercises and the exercise data to filter for incorrect inputs in the database.
            val completeAssignedExercises = program.assignedExercises.mapNotNull { assigned ->
                // For each assigned exercise, find the matching exercise by ID.
                val exercise = allExercises.find { it.id == assigned.exerciseId }

                // If the base exercise exists build a complete AssignedExercise object with the personalised details.
                exercise?.let {
                    AssignedExercise(
                        exerciseId = it.id,
                        reps = assigned.reps,
                        sets = assigned.sets,
                        holdTime = assigned.holdTime
                    )
                }
            }

            // Replace the original programs  assigned exercises with the checked list without input errors.
            val updatedProgram = program.copy(assignedExercises = completeAssignedExercises)
            val exerciseMap = allExercises.associateBy { it.id } // create a map of exercises able to be referenced easily.

            result = Pair(updatedProgram, exerciseMap) // Update the result pair with the updated program and the exercise map.

        } catch (e: Exception) {
            // If an error occurs, log the error and return null.
            Log.e("userProgram", "Error fetching the users program or exercises list: ${e.message}")
        }
        return result // Return the final result or null.
    }
}
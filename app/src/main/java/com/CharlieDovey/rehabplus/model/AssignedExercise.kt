package com.charliedovey.rehabplus.model

/**
 * AssignedExercise is a custom version of a base Exercise,
 * allowing a physiotherapist to specify user-specific reps, sets, and hold time.
 * This uses composition, referencing the Exercise by ID.
 */
data class AssignedExercise(
    val exerciseId: String,
    var reps: Int,
    var sets: Int,
    var holdTime: Int = 0
)
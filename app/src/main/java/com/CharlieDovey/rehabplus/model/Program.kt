package com.charliedovey.rehabplus.model

data class Program(
    val id: String,
    val name: String,
    val instructions: String,
    var assignedExercises: List<AssignedExercise> = emptyList(),
    var isComplete: Boolean = false
)
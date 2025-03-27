package com.charliedovey.rehabplus.model

data class Program(
    val id: String,
    val name: String,
    val instructions: String,
    var exerciseIds: List<String> = emptyList(),
    var isComplete: Boolean = false
)
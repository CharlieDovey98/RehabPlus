package com.charliedovey.rehabplus.model

data class Exercise(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    var sets: Int,
    var reps: Int,
    var holdTime: Int,
    val videoUrl: String,
    var isComplete: Boolean = false
)

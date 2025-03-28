package com.charliedovey.rehabplus.model

data class Exercise(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val videoUrl: String,
)
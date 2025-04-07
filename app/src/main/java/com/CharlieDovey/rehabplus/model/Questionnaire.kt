package com.charliedovey.rehabplus.model

data class Questionnaire(
    val userEmail: String,
    val userName: String,
    val userDateOfBirth: String,
    val dateOfSubmission: String,
    val responses: Map<String, Int>
)

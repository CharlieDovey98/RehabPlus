package com.charliedovey.rehabplus.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    var completedQuestionnaire: Boolean = false,
    var assignedProgramIds: List<String> = emptyList(),
    val earnedBadgeIds: List<String>
)
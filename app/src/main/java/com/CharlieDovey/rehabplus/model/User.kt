package com.charliedovey.rehabplus.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    var completedQuestionnaire: Boolean = false,
    var assignedProgramId: String,
    val earnedBadgeIds: List<String>
)
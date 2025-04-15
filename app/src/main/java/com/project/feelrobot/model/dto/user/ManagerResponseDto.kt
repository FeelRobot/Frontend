package com.project.feelrobot.model.dto.user

data class ManagerResponseDto(
    val userId: String,
    val email: String,
    val name: String,
    val role: Int
)
package com.project.feelrobot.model.dto

data class RegisterDto(
    val id: String,
    val password: String,
    val email: String,
    val name: String,
    val role: Int
)

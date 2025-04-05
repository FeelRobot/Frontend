package com.project.feelrobot.model.dto.user

data class StudentResponseDto(
    val userId: String,
    val email: String,
    val name: String,
    val birth: String,
    val sex: Int,
    val managerId: String
)
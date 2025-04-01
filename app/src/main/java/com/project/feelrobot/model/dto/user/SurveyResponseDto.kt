package com.project.feelrobot.model.dto.user

data class SurveyResponseDto (
    val userId: String,
    val birth: String,
    val sex: Int,
    val managerId: String
)
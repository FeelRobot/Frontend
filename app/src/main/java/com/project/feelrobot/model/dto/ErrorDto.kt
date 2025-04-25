package com.project.feelrobot.model.dto

data class ErrorDto (
    val errorCode: Int,
    val errorMessage: String,
    val timestamp: String
)
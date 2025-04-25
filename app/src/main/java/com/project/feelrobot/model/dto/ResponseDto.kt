package com.project.feelrobot.model.dto

data class ResponseDto<T>(
    val data: T,
    val httpStatus: Int
)

package com.project.feelrobot.model.dto

data class KakaoRequestDto (
    val grant_type: String,
    val client_id: String,
    val redirect_uri: String,
    val code: String,
    val client_secret: String
)
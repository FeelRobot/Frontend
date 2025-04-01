package com.project.feelrobot.model.dto.sign

data class KakaoResponseDto(
    val token_type: String,
    val access_token: String,
    val id_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val refresh_token_expires_in: Int,
    val scope: String
)
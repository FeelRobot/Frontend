package com.project.feelrobot.model.request

data class SocialLoginRequest(
    val provider: String, // ex. google, kakao
    val tocken: String // OAuth 제공자 access token
)

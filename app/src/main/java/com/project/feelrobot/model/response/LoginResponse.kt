package com.project.feelrobot.model.response

import com.project.feelrobot.model.entity.User

data class LoginResponse(
    val user: User,
    val token: String // jwt/OAuth token
)

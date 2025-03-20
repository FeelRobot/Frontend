package com.project.feelrobot.network

import com.project.feelrobot.model.dto.LoginRequestDto
import com.project.feelrobot.model.dto.RegisterDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {
    @POST("/sign/register")
    suspend fun register(@Body registerDto: RegisterDto): Response<ResponseBody>

    @POST("/sign/login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): Response<ResponseBody>
}
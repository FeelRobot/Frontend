package com.project.feelrobot.network

import com.project.feelrobot.model.dto.LoginRequestDto
import com.project.feelrobot.model.dto.LoginResponseDto
import com.project.feelrobot.model.dto.LogoutRequestDto
import com.project.feelrobot.model.dto.RefreshDto
import com.project.feelrobot.model.dto.RegisterDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {
    @POST("/sign/register")
    suspend fun register(@Body registerDto: RegisterDto):Response<String>

    @POST("/sign/login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): Response<LoginResponseDto>

    @POST("/sign/logout")
    suspend fun logout(@Body logoutRequest: LogoutRequestDto): Response<String>

    @POST("/sign/refresh")
    suspend fun refreshToken(@Body refreshDto: RefreshDto): Response<LoginResponseDto>
}
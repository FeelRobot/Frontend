package com.project.feelrobot.network

import com.project.feelrobot.model.dto.sign.KakaoRequestDto
import com.project.feelrobot.model.dto.sign.KakaoResponseDto
import com.project.feelrobot.model.dto.sign.LoginRequestDto
import com.project.feelrobot.model.dto.sign.LoginResponseDto
import com.project.feelrobot.model.dto.sign.LogoutRequestDto
import com.project.feelrobot.model.dto.sign.MailDto
import com.project.feelrobot.model.dto.sign.RefreshDto
import com.project.feelrobot.model.dto.sign.RegisterDto
import com.project.feelrobot.model.dto.user.SurveyResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @POST("sign/register")
    suspend fun register(@Body registerDto: RegisterDto): Response<String>

    @POST("sign/login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): Response<LoginResponseDto>

    @POST("sign/logout")
    suspend fun logout(@Body logoutRequest: LogoutRequestDto): Response<String>

    @POST("sign/refresh")
    suspend fun refreshToken(@Body refreshDto: RefreshDto): Response<LoginResponseDto>

    @POST("sign/kakao/callback")
    suspend fun kakaoLogin(@Body request: KakaoRequestDto): Response<KakaoResponseDto>

    @GET("sign/check/{id}")
    suspend fun checkIdDuplication(@Path("id") id: String): Response<String>

    @POST("sign/mail")
    suspend fun sendMail(@Body mailDto: MailDto): Response<String>

    @GET("sign/check/{email}/{number}")
    suspend fun verifyMail(
        @Path("email") email: String, @Path("number") number: Int
    ): Response<String>

    @POST("user/survey")
    suspend fun survey(@Body surveyResponseDto: SurveyResponseDto): Response<String>
}
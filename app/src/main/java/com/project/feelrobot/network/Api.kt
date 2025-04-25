package com.project.feelrobot.network

import com.project.feelrobot.model.dto.ResponseDto
import com.project.feelrobot.model.dto.sign.LoginRequestDto
import com.project.feelrobot.model.dto.sign.LoginResponseDto
import com.project.feelrobot.model.dto.sign.LogoutRequestDto
import com.project.feelrobot.model.dto.sign.MailDto
import com.project.feelrobot.model.dto.sign.RefreshDto
import com.project.feelrobot.model.dto.sign.RefreshTokenResponseDto
import com.project.feelrobot.model.dto.sign.RegisterDto
import com.project.feelrobot.model.dto.user.SurveyResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @POST("sign/register")
    suspend fun register(@Body registerDto: RegisterDto): Response<ResponseDto<Unit>>

    @POST("sign/login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): Response<LoginResponseDto>

    @POST("sign/logout")
    suspend fun logout(@Body refreshToken: String): Response<ResponseDto<Unit>> // 서버에러

    @POST("sign/refresh")
    suspend fun refreshToken(@Body refreshDto: RefreshDto): Response<RefreshTokenResponseDto>

    @GET("sign/check/{id}")
    suspend fun checkIdDuplication(@Path("id") id: String): Response<ResponseDto<Unit>>

    @POST("sign/mail")
    suspend fun sendMail(@Body mailDto: MailDto): Response<ResponseDto<Unit>>

    @GET("sign/check/{email}/{number}")
    suspend fun verifyMail(
        @Path("email") email: String, @Path("number") number: Int
    ): Response<ResponseDto<Unit>>

    @POST("user/survey")
    suspend fun survey(@Body surveyResponseDto: SurveyResponseDto): Response<ResponseDto<Unit>>

    @GET("user/info") // 수정 필요함
    suspend fun userInfo(): Response<Any>
}
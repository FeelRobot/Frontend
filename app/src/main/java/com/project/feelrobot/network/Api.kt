package com.project.feelrobot.network

import com.project.feelrobot.model.dto.ResponseDto
import com.project.feelrobot.model.dto.sign.LoginRequestDto
import com.project.feelrobot.model.dto.sign.LoginResponseDto
import com.project.feelrobot.model.dto.sign.MailDto
import com.project.feelrobot.model.dto.sign.RefreshDto
import com.project.feelrobot.model.dto.sign.RefreshTokenResponseDto
import com.project.feelrobot.model.dto.sign.RegisterDto
import com.project.feelrobot.model.dto.user.CheckPasswordDto
import com.project.feelrobot.model.dto.user.SurveyResponseDto
import com.project.feelrobot.model.dto.user.UpdateEmailDto
import com.project.feelrobot.model.dto.user.UpdatePasswordDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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
    suspend fun sendMail(@Body mailDto: MailDto): Response<ResponseDto<String>>

    @GET("sign/check/{email}/{number}")
    suspend fun verifyMail(
        @Path("email") email: String, @Path("number") number: Int
    ): Response<ResponseDto<String>>

    @POST("user/survey")
    suspend fun survey(@Body surveyResponseDto: SurveyResponseDto): Response<ResponseDto<Unit>>

    @GET("user/info") // 수정 필요함
    suspend fun userInfo(): Response<Any>

    @POST("user/password")
    suspend fun checkPassword(@Body checkPasswordDto: CheckPasswordDto): Response<ResponseDto<String>>

    @GET("sign/exist/{email}")
    suspend fun existEmail(@Path("email") email: String): Response<ResponseDto<String>>

    @GET("sign/find/{email}")
    suspend fun findIdByEmail(@Path("email") email: String): Response<ResponseDto<String>>

    @GET("sign/check")
    suspend fun existUserByIdEmail(
        @Query("id") id: String, @Query("email") email: String
    ): Response<ResponseDto<String>>

    @PATCH("sign/update/password")
    suspend fun updatePassword(@Body dto: UpdatePasswordDto): Response<ResponseDto<String>>

    @PATCH("user/update/email")
    suspend fun updateEmail(@Body dto: UpdateEmailDto): Response<ResponseDto<String>>

}
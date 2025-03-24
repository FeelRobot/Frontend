package com.project.feelrobot.service

import android.content.Context
import android.util.Log
import com.project.feelrobot.R
import com.project.feelrobot.model.dto.KakaoRequestDto
import com.project.feelrobot.network.RetrofitInstance
import com.project.feelrobot.storage.JwtTokenManager

suspend fun sendTokenToServer(accessToken: String, context: Context, onSuccess: (String) -> Unit) {
    try {
        val api = RetrofitInstance.getApi(context)
        val response = api.kakaoLogin(
            KakaoRequestDto(
                grant_type = "authorization_code",
                client_id = context.getString(R.string.kakao_client_id),  // REST API 키
                redirect_uri = "kakao${context.getString(R.string.kakao_native_app_key)}://oauth",  // Redirect URI
                code = accessToken,
                client_secret = context.getString(R.string.kakao_client_secret)  // Client Secret
            )
        )

        if (response.isSuccessful && response.body() != null) {
            val responseData = response.body()!!

            // JWT 저장
            JwtTokenManager(context).saveTokens(
                responseData.access_token,
                responseData.refresh_token
            )

            // 로그인 성공 → 홈 화면 이동
            Log.i("KakaoLogin", "로그인 성공: JWT 저장 완료")
            onSuccess("home") // 성공 시 home으로 이동
        } else {
            // 회원가입 필요 → 이메일 추출 후 회원가입 화면 이동
            val email = response.errorBody()?.string() ?: "unknown_email"
            onSuccess("kakaoSignup?email=$email")
        }
    } catch (e: Exception) {
        Log.e("KakaoLogin", "서버 요청 실패: ${e.message}")
    }
}

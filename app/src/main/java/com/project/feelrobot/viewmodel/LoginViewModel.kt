package com.project.feelrobot.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.project.feelrobot.R
import com.project.feelrobot.model.dto.sign.KakaoRequestDto
import com.project.feelrobot.model.dto.sign.LoginRequestDto
import com.project.feelrobot.network.RetrofitInstance
import com.project.feelrobot.storage.JwtTokenManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    fun login(id: String, password: String, isAutoLogin: Boolean, context: Context, navController: NavController) {
        viewModelScope.launch {
            val api = RetrofitInstance.getApi(context) // JwtInterceptor 적용된 Retrofit 인스턴스 사용
            val response = api.login(LoginRequestDto(id, password))

            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    // JWT 저장
                    JwtTokenManager(context).apply {
                        saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                        setAutoLogin(isAutoLogin)
                    }
                    println("로그인 성공, 토큰 저장됨")

                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            } else {
                println("로그인 실패: ${response.errorBody()?.string()}")
            }
        }
    }

    // 백엔드에 인가 코드를 전달하여 카카오 로그인 처리
    fun kakaoLoginWithAuthCode(code: String, context: Context, navController: NavController) {
        viewModelScope.launch {
            try {
                val api = RetrofitInstance.getApi(context)
                val request = KakaoRequestDto(
                    grant_type = "authorization_code",
                    client_id = context.getString(R.string.kakao_client_id),
                    redirect_uri = context.getString(R.string.kakao_redirect_uri),
                    code = code,
                    client_secret = context.getString(R.string.kakao_client_secret)
                )
                val response = api.kakaoLogin(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        if (body.access_token.isNotEmpty() && body.refresh_token.isNotEmpty()) {
                            // 회원가입 완료(로그인): JWT 토큰이 발급됨
                            navController.navigate("home") { popUpTo("login") { inclusive = true } }
                        } else {
                            // 미가입: access_token 필드에 이메일이 담겨 있음 -> 회원가입 화면으로 이동해 추가 정보 입력 받음
                            navController.navigate("signup?email=${body.access_token}") {
                                popUpTo("login") { inclusive = true }
                            }

                        }
                    }
                } else {
                    Log.e("LoginViewModel", "카카오 로그인 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "카카오 로그인 에러: ${e.message}")
            }
        }
    }
}
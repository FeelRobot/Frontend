package com.project.feelrobot.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.project.feelrobot.model.dto.LoginRequestDto
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
}
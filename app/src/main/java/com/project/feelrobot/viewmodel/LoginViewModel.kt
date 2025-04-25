package com.project.feelrobot.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.gson.Gson
import com.project.feelrobot.model.dto.ErrorDto
import com.project.feelrobot.model.dto.sign.LoginRequestDto
import com.project.feelrobot.network.RetrofitInstance
import com.project.feelrobot.storage.JwtTokenManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    fun login(
        id: String,
        password: String,
        isAutoLogin: Boolean,
        context: Context,
        navController: NavController
    ) {
        viewModelScope.launch {
            try {
                val api = RetrofitInstance.getApi(context) // JwtInterceptor 적용된 Retrofit 인스턴스 사용
                val response = api.login(LoginRequestDto(id, password))

                if (response.isSuccessful) {
                    // 200: LoginResponseDto 로 바로 역직렬화
                    response.body()?.let { loginResponse ->
                        // JWT 저장
                        JwtTokenManager(context).apply {
                            saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                            setAutoLogin(isAutoLogin)
                        }
                        Log.d("LoginViewModel", "로그인 성공, 토큰 저장됨")

                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } ?: run {
                        Toast.makeText(context, "로그인 응답이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 400/500: ErrorDto 로 파싱
                    val errJson = response.errorBody()?.charStream()
                    val errorDto = Gson().fromJson(errJson, ErrorDto::class.java)
                    Log.e(
                        "LoginViewModel",
                        "로그인 실패: code=${errorDto.errorCode}, msg=${errorDto.errorMessage}"
                    )
                    Toast.makeText(context, errorDto.errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "통신 예외: ${e.message}", e)
                Toast.makeText(context, "서버 에러: ${e.message ?: "알 수 없는 오류"}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun logout(
        refreshToken: String, context: Context, navController: NavController
    ) {
        viewModelScope.launch {
            try {
                val api = RetrofitInstance.getApi(context)
                val response = api.logout(refreshToken)

                if (response.isSuccessful) {
                    // 200: ResponseDto<Unit> 파싱
                    val body = response.body()!!
                    Log.d("LoginViewModel", "로그아웃 성공: httpStatus=${body.httpStatus}")

                    // 토큰 삭제 및 화면 이동
                    JwtTokenManager(context).clearTokens()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    // 400/500: ErrorDto 파싱
                    val errJson = response.errorBody()?.charStream()
                    val errorDto = Gson().fromJson(errJson, ErrorDto::class.java)
                    Log.e(
                        "LoginViewModel",
                        "로그아웃 실패: code=${errorDto.errorCode}, msg=${errorDto.errorMessage}"
                    )
                    Toast.makeText(
                        context, errorDto.errorMessage, Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                // 네트워크 / 파싱 오류
                Log.e("LoginViewModel", "로그아웃 예외: ${e.message}", e)
                Toast.makeText(
                    context, "로그아웃 중 오류: ${e.message ?: "알 수 없는 오류"}", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

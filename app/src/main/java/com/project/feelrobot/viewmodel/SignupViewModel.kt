package com.project.feelrobot.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.feelrobot.model.dto.RegisterDto
import com.project.feelrobot.network.RetrofitInstance
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    fun registerUser(
        registerDto: RegisterDto, context: Context, onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("SignupViewModel", "회원가입 요청: $registerDto") // 요청 데이터 로그
                val response = RetrofitInstance.api.register(registerDto)

                if (response.isSuccessful) {
                    Log.d("SignupViewModel", "회원가입 성공: ${response.body()}")
                    Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                    onSuccess() // 회원가입 성공 시, 후속 액션
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "회원가입 실패"
                    Log.e("SignupViewModel", "회원가입 실패: $errorMessage") // 에러 로그 추가
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SignupViewModel", "서버 연결 오류: ${e.message}", e) // 네트워크 오류 확인
                Toast.makeText(context, "서버 에러: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

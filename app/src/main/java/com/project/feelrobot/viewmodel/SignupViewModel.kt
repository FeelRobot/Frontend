package com.project.feelrobot.viewmodel

import android.content.Context
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
                val response = RetrofitInstance.api.register(registerDto)

                if (response.isSuccessful) {
                    Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                    onSuccess() // 회원가입 성공 시, 후속 액션
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "회원가입 실패"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "서버 에러: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

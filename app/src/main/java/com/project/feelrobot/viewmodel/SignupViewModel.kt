package com.project.feelrobot.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.feelrobot.model.dto.sign.MailDto
import com.project.feelrobot.model.dto.sign.RegisterDto
import com.project.feelrobot.model.dto.user.SurveyResponseDto
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

    fun submitSurvey(
        surveyResponseDto: SurveyResponseDto, context: Context, onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.survey(surveyResponseDto)
                if (response.isSuccessful) {
                    Log.d("SignupViewModel", "설문조사 제출 성공: ${response.body()}")
                    Toast.makeText(context, "설문조사 제출 성공!", Toast.LENGTH_SHORT).show()
                    onSuccess() // 설문조사 제출 성공 시, 후속 액션
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "설문조사 제출 실패"
                    Log.e("SignupViewModel", "설문조사 제출 실패: $errorMessage") // 에러 로그 추가
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SignupViewModel", "서버 연결 오류: ${e.message}", e) // 네트워크 오류 확인
                Toast.makeText(context, "서버 에러: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 아이디 중복 확인
    fun checkIdDuplication(id: String, context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getApi(context).checkIdDuplication(id)
                if (response.isSuccessful) {
                    // 성공일 경우: "아이디 사용 가능"
                    Toast.makeText(context, response.body() ?: "아이디 사용 가능", Toast.LENGTH_SHORT)
                        .show()
                    onResult(true)
                } else {
                    // 실패일 경우: "이미 존재하는 아이디입니다." 등
                    val errorMsg = response.errorBody()?.string() ?: "아이디 중복 확인 실패"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("SignupViewModel", "중복 확인 오류: ${e.message}")
                Toast.makeText(context, "중복 확인 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
        }
    }


    // 이메일 인증번호 전송
    fun sendEmailAuth(email: String, context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getApi(context).sendMail(MailDto(email))
                if (response.isSuccessful) {
                    // "메일 전송 성공"
                    Toast.makeText(context, response.body() ?: "메일 전송 성공", Toast.LENGTH_SHORT)
                        .show()
                    onResult(true)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "메일 전송 실패"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("SignupViewModel", "이메일 인증 오류: ${e.message}")
                Toast.makeText(context, "이메일 인증 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
        }
    }


    // 이메일 인증번호 검증
    fun verifyEmailAuth(
        email: String, code: String, context: Context, onResult: (Boolean) -> Unit
    ) {
        // code를 Int로 변환 시도
        val number = code.toIntOrNull()
        if (number == null) {
            Toast.makeText(context, "인증번호는 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show()
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getApi(context).verifyMail(email, number)
                if (response.isSuccessful) {
                    // "이메일 인증 성공"
                    Toast.makeText(context, response.body() ?: "이메일 인증 성공", Toast.LENGTH_SHORT)
                        .show()
                    onResult(true)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "이메일 인증 실패"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("SignupViewModel", "이메일 인증번호 확인 오류: ${e.message}")
                Toast.makeText(context, "이메일 인증번호 확인 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
        }
    }


}

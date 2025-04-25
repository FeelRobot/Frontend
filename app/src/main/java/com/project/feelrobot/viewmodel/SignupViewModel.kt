package com.project.feelrobot.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.project.feelrobot.model.dto.ErrorDto
import com.project.feelrobot.model.dto.sign.LoginRequestDto
import com.project.feelrobot.model.dto.sign.MailDto
import com.project.feelrobot.model.dto.sign.RegisterDto
import com.project.feelrobot.model.dto.user.SurveyResponseDto
import com.project.feelrobot.network.RetrofitInstance
import com.project.feelrobot.storage.JwtTokenManager
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    fun registerUser(
        registerDto: RegisterDto,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                Log.d("SignupViewModel", "회원가입 요청: $registerDto") // 요청 데이터 로그
                val response = RetrofitInstance.api.register(registerDto)

                if (response.isSuccessful) {
                    // 1) HTTP 200: ResponseDto<Unit> 로 파싱 완료
                    val body = response.body()!!
                    Log.d(
                        "SignupViewModel",
                        "회원가입 성공: httpStatus=${body.httpStatus}, data=${body.data}"
                    )
                    Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()

                    loginAfterRegister(registerDto.id, registerDto.password, context, onSuccess = {
                        onSuccess() // 최종 가입+로그인 성공
                    }, onFailure = { errMsg ->
                        onFailure("로그인 실패: $errMsg")
                    })
                } else {
                    // HTTP 400/500: errorBody에 ErrorDto 스키마
                    val errJson = response.errorBody()?.charStream()
                    val errorDto = Gson().fromJson(errJson, ErrorDto::class.java)

                    Log.e(
                        "SignupViewModel",
                        "회원가입 실패: code=${errorDto.errorCode}, msg=${errorDto.errorMessage}"
                    )
                    Toast.makeText(context, errorDto.errorMessage, Toast.LENGTH_SHORT).show()
                    onFailure(errorDto.errorMessage)
                }
            } catch (e: Exception) {
                // 네트워크 실패나 JSON 파싱 에러
                Log.e("SignupViewModel", "서버 연결 오류: ${e.message}", e) // 네트워크 오류 확인
                val msg = e.message ?: "알 수 없는 오류"
                Toast.makeText(context, "서버 에러: $msg", Toast.LENGTH_SHORT).show()
                onFailure(msg)
            }
        }
    }

    // 회원가입 직후 일반 로그인용 함수
    private fun loginAfterRegister(
        id: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getApi(context).login(LoginRequestDto(id, password))
                if (response.isSuccessful) {
                    response.body()?.let { loginResp ->
                        // 토큰 저장하여 로그인
                        JwtTokenManager(context).apply {
                            saveTokens(loginResp.accessToken, loginResp.refreshToken)
                            setAutoLogin(false) // 혹은 true
                        }
                        Log.d("SignupViewModel", "로그인 성공")
                        onSuccess()
                    } ?: run {
                        onFailure("로그인 응답이 비어있습니다.")
                    }
                } else {
                    val errJson = response.errorBody()?.charStream()
                    val errorDto = Gson().fromJson(errJson, ErrorDto::class.java)
                    onFailure(errorDto.errorMessage)
                }
            } catch (e: Exception) {
                onFailure("자동 로그인 오류: ${e.message}")
            }
        }
    }

    fun submitSurvey(
        surveyResponseDto: SurveyResponseDto, context: Context
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.survey(surveyResponseDto)
                if (response.isSuccessful) {
                    // 200: ResponseDto<Unit> 파싱
                    val body = response.body()!!
                    Log.d(
                        "SignupViewModel", "설문조사 제출 성공: httpStatus=${body.httpStatus}"
                    )
                    Toast.makeText(
                        context, "설문조사 제출 성공!", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // 400/500: ErrorDto 로 파싱
                    val errJson = response.errorBody()?.charStream()
                    val errorDto = Gson().fromJson(errJson, ErrorDto::class.java)
                    Log.e(
                        "SignupViewModel",
                        "설문조사 제출 실패: code=${errorDto.errorCode}, msg=${errorDto.errorMessage}"
                    )
                    Toast.makeText(
                        context, errorDto.errorMessage, Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                // 네트워크/파싱 예외 처리
                Log.e("SignupViewModel", "설문조사 제출 중 오류: ${e.message}", e)
                Toast.makeText(
                    context, "서버 에러: ${e.message ?: "알 수 없는 오류"}", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // 아이디 중복 확인
    fun checkIdDuplication(id: String, context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getApi(context).checkIdDuplication(id)
                when (response.code()) {
                    200 -> {
                        // 200: 사용 가능한 ID
                        Toast.makeText(context, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
                        onResult(true)
                    }

                    400 -> {
                        // 400: 중복된 ID
                        Toast.makeText(context, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show()
                        onResult(false)
                    }

                    else -> {
                        // 500 등 기타 오류
                        Toast.makeText(context, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT)
                            .show()
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("SignupViewModel", "중복 확인 예외: ${e.message}", e)
                Toast.makeText(
                    context, "중복 확인 오류: ${e.message ?: "알 수 없는 오류"}", Toast.LENGTH_SHORT
                ).show()
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
                    // HTTP 200: 성공 DTO
                    val body = response.body()!!   // ResponseDto<Unit>
                    Log.d(
                        "SignupViewModel", "메일 전송 성공: httpStatus=${body.httpStatus}"
                    )
                    Toast.makeText(
                        context, "메일 전송 성공!", Toast.LENGTH_SHORT
                    ).show()
                    onResult(true)
                } else {
                    // HTTP 400/500: ErrorDto 파싱
                    val errJson = response.errorBody()?.charStream()
                    val errorDto = Gson().fromJson(errJson, ErrorDto::class.java)
                    Log.e(
                        "SignupViewModel",
                        "메일 전송 실패: code=${errorDto.errorCode}, msg=${errorDto.errorMessage}"
                    )
                    Toast.makeText(
                        context, errorDto.errorMessage, Toast.LENGTH_SHORT
                    ).show()
                    onResult(false)
                }
            } catch (e: Exception) {
                // 네트워크 또는 파싱 오류
                Log.e("SignupViewModel", "이메일 인증 오류: ${e.message}")
                Toast.makeText(
                    context, "이메일 인증 오류: ${e.message ?: "알 수 없는 오류"}", Toast.LENGTH_SHORT
                ).show()
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
                    // 200: ResponseDto<Unit> 파싱
                    val body = response.body()!!
                    Log.d(
                        "SignupViewModel", "이메일 인증 성공: httpStatus=${body.httpStatus}"
                    )
                    Toast.makeText(context, "이메일 인증 성공!", Toast.LENGTH_SHORT).show()
                    onResult(true)
                } else {
                    // 400/500: ErrorDto 파싱
                    val errJson = response.errorBody()?.charStream()
                    val errorDto = Gson().fromJson(errJson, ErrorDto::class.java)
                    Log.e(
                        "SignupViewModel",
                        "이메일 인증 실패: code=${errorDto.errorCode}, msg=${errorDto.errorMessage}"
                    )
                    Toast.makeText(context, errorDto.errorMessage, Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            } catch (e: Exception) {
                // 네트워크/파싱 예외
                Log.e("SignupViewModel", "이메일 인증 오류: ${e.message}", e)
                Toast.makeText(
                    context, "이메일 인증 오류: ${e.message ?: "알 수 없는 오류"}", Toast.LENGTH_SHORT
                ).show()
                onResult(false)
            }
        }
    }
}

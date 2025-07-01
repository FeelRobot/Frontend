package com.project.feelrobot.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.project.feelrobot.model.dto.ErrorDto
import com.project.feelrobot.model.dto.ResponseDto
import com.project.feelrobot.model.dto.user.CheckPasswordDto
import com.project.feelrobot.model.dto.user.ManagerResponseDto
import com.project.feelrobot.model.dto.user.StudentResponseDto
import com.project.feelrobot.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 상태 정의
sealed class UserInfoState {
    object Loading : UserInfoState()
    data class Student(val data: StudentResponseDto) : UserInfoState()
    data class Manager(val data: ManagerResponseDto) : UserInfoState()
    data class Error(val message: String) : UserInfoState()
}

sealed class PasswordCheckState {
    object Idle : PasswordCheckState()
    object Loading : PasswordCheckState()
    object Success : PasswordCheckState()
    data class Error(val message: String) : PasswordCheckState()
}

// ViewModel
class UserInfoViewModel(private val appContext: Context) : ViewModel() {
    private val _userInfoState = MutableStateFlow<UserInfoState>(UserInfoState.Loading)
    val userInfoState = _userInfoState.asStateFlow()

    private val _passwordState = MutableStateFlow<PasswordCheckState>(PasswordCheckState.Idle)
    val passwordState = _passwordState.asStateFlow()

    fun fetchUserInfo() {
        viewModelScope.launch {
            _userInfoState.value = UserInfoState.Loading
            try {
                val response = RetrofitInstance.getApi(appContext).userInfo() // /user/info
                if (response.isSuccessful && response.body() != null) {
                    // 정상 응답 처리
                    val gson = Gson()
                    val jsonStr = gson.toJson(response.body())  // Any → JSON
                    val asJsonObj = gson.fromJson(jsonStr, JsonObject::class.java)
                    when (val role = asJsonObj.get("role").asInt) {
                        0 -> {
                            // 학생 DTO
                            val student = gson.fromJson(jsonStr, StudentResponseDto::class.java)
                            _userInfoState.value = UserInfoState.Student(student)
                        }

                        1 -> {
                            // 보호자 DTO
                            val manager = gson.fromJson(jsonStr, ManagerResponseDto::class.java)
                            _userInfoState.value = UserInfoState.Manager(manager)
                        }

                        else -> {
                            _userInfoState.value = UserInfoState.Error("알 수 없는 role 값: $role")
                        }
                    }
                } else {
                    val statusCode = response.code()
                    val errMsg = response.errorBody()?.string()?.ifEmpty { "HTTP $statusCode" }
                        ?: "userInfo API error"
                    Log.e(
                        "UserInfoViewModel", "API 응답 실패: statusCode=$statusCode, errorBody=$errMsg"
                    )

                    _userInfoState.value = UserInfoState.Error(errMsg)
                }
            } catch (e: Exception) {
                Log.e("UserInfoViewModel", "예외 발생: ${e.message}", e)
                _userInfoState.value = UserInfoState.Error(e.message ?: "unknown error")
            }
        }
    }

    fun checkPassword(password: String) {
        viewModelScope.launch {
            _passwordState.value = PasswordCheckState.Loading
            try {
                val res = RetrofitInstance.getApi(appContext)
                    .checkPassword(CheckPasswordDto(password)) // Response<ResponseDto<String>>
                if (res.isSuccessful) {
                    // body.data를 로그로 확인하거나 무시
                    val wrapper: ResponseDto<String> = res.body()!!
                    Log.d("UserInfoVM", "checkPassword success: ${wrapper.data}")
                    _passwordState.value = PasswordCheckState.Success
                } else {
                    val err = Gson().fromJson(res.errorBody()!!.charStream(), ErrorDto::class.java)
                    Log.e("UserInfoVM", "checkPassword failed: ${err.errorMessage}")
                    _passwordState.value = PasswordCheckState.Error(err.errorMessage)
                }
            } catch (e: Exception) {
                Log.e("UserInfoVM", "checkPassword exception", e)
                _passwordState.value = PasswordCheckState.Error(e.message ?: "네트워크 오류")
            }
        }
    }
}

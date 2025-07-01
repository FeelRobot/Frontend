// FindViewModel.kt
package com.project.feelrobot.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.project.feelrobot.model.dto.ErrorDto
import com.project.feelrobot.model.dto.ResponseDto
import com.project.feelrobot.model.dto.sign.MailDto
import com.project.feelrobot.model.dto.user.FindIdResponseDto
import com.project.feelrobot.model.dto.user.UpdateEmailDto
import com.project.feelrobot.model.dto.user.UpdatePasswordDto
import com.project.feelrobot.network.RetrofitInstance
import kotlinx.coroutines.launch

class FindViewModel : ViewModel() {

    // 이메일 인증번호 전송
    fun sendEmailAuth(email: String, context: Context, onNext: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val res = RetrofitInstance.getApi(context).sendMail(MailDto(email))
                if (res.isSuccessful) {
                    val body: ResponseDto<String> = res.body()!!
                    Log.d("FindVM", "existEmail data='${body.data}' httpStatus=${body.httpStatus}")
                    Toast.makeText(context, "인증번호가 발송되었습니다.", Toast.LENGTH_SHORT).show()
                    onNext()
                } else {
                    val err = Gson().fromJson(res.errorBody()!!.charStream(), ErrorDto::class.java)
                    Log.e("FindVM", "existEmail failed: ${err.errorMessage}")
                    onError(err.errorMessage)
                }
            } catch (e: Exception) {
                Log.e("FindVM", "existEmail exception", e)
                onError("네트워크 오류: ${e.message}")
            }
        }
    }

    //1. 이메일 존재 여부 확인 → 인증번호 발송
    fun requestIdByEmail(
        email: String, context: Context, onNext: () -> Unit, onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val res = RetrofitInstance.getApi(context).existEmail(email) // ResponseDto<String>
                if (res.isSuccessful) {
                    val body: ResponseDto<String> = res.body()!!
                    Log.d("FindVM", "existEmail data='${body.data}' httpStatus=${body.httpStatus}")
                    Toast.makeText(context, "인증번호가 발송되었습니다.", Toast.LENGTH_SHORT).show()
                    onNext()
                } else {
                    val err = Gson().fromJson(res.errorBody()!!.charStream(), ErrorDto::class.java)
                    Log.e("FindVM", "existEmail failed: ${err.errorMessage}")
                    onError(err.errorMessage)
                }
            } catch (e: Exception) {
                Log.e("FindVM", "existEmail exception", e)
                onError("네트워크 오류: ${e.message}")
            }
        }
    }

    // 2. 이메일 인증번호 검증
    fun verifyMailCode(
        email: String, code: String, context: Context, onNext: () -> Unit, onError: (String) -> Unit
    ) {
        val num = code.toIntOrNull()
        if (num == null) {
            Log.e("FindVM", "verifyMailCode: code not number")
            onError("인증번호는 숫자로 입력해주세요.")
            return
        }
        viewModelScope.launch {
            try {
                val res = RetrofitInstance.getApi(context).verifyMail(email, num)
                if (res.isSuccessful) {
                    val body: ResponseDto<String> = res.body()!!
                    Log.d("FindVM", "verifyMail success data='${body.data}'")
                    Toast.makeText(context, "이메일 인증 성공!", Toast.LENGTH_SHORT).show()
                    onNext()
                } else {
                    val err = Gson().fromJson(res.errorBody()!!.charStream(), ErrorDto::class.java)
                    Log.e("FindVM", "verifyMail failed: ${err.errorMessage}")
                    onError(err.errorMessage)
                }
            } catch (e: Exception) {
                Log.e("FindVM", "verifyMail exception", e)
                onError("네트워크 오류: ${e.message}")
            }
        }
    }

    // 3. 이메일 → 아이디 조회
    fun fetchIdByEmail(
        email: String, context: Context, onResult: (String) -> Unit, onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val res =
                    RetrofitInstance.getApi(context).findIdByEmail(email) // ResponseDto<String>
                if (res.isSuccessful) {
                    val body = res.body()!!
                    val jsonStr = body.data
                    val idDto = Gson().fromJson(jsonStr, FindIdResponseDto::class.java)

                    onResult(idDto.id)      // 순수 id 문자열 전달
                } else {
                    val err = Gson().fromJson(res.errorBody()!!.charStream(), ErrorDto::class.java)
                    Log.e("FindVM", "findIdByEmail failed: ${err.errorMessage}")
                    onError(err.errorMessage)
                }
            } catch (e: Exception) {
                Log.e("FindVM", "findIdByEmail exception", e)
                onError("네트워크 오류: ${e.message}")
            }
        }
    }

    // 4. 이메일 유효성 검사 → 인증번호 발송
    fun requestPasswordReset(
        id: String, email: String, context: Context, onNext: () -> Unit, onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val res = RetrofitInstance.getApi(context)
                    .existUserByIdEmail(id, email) // ResponseDto<String>
                if (res.isSuccessful) {
                    val body: ResponseDto<String> = res.body()!!
                    Log.d("FindVM", "existUserByIdEmail data='${body.data}'")
                    Toast.makeText(context, "인증번호가 발송되었습니다.", Toast.LENGTH_SHORT).show()
                    onNext()
                } else {
                    val err = Gson().fromJson(res.errorBody()!!.charStream(), ErrorDto::class.java)
                    Log.e("FindVM", "existUserByIdEmail failed: ${err.errorMessage}")
                    onError(err.errorMessage)
                }
            } catch (e: Exception) {
                Log.e("FindVM", "existUserByIdEmail exception", e)
                onError("네트워크 오류: ${e.message}")
            }
        }
    }

    // 5. 비밀번호 찾기: 인증번호 검증 (이메일 인증 재사용)
    fun verifyPasswordCode(
        email: String, code: String, context: Context, onNext: () -> Unit, onError: (String) -> Unit
    ) {
        verifyMailCode(email, code, context, onNext, onError)
    }

    // 6. 비밀번호 변경
    fun updatePassword(
        id: String,
        newPassword: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val dto = UpdatePasswordDto(id, newPassword)
                val res = RetrofitInstance.getApi(context).updatePassword(dto) // ResponseDto<Unit>
                if (res.isSuccessful) {
                    Log.d("FindVM", "updatePassword success")
                    Toast.makeText(context, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    val err = Gson().fromJson(res.errorBody()!!.charStream(), ErrorDto::class.java)
                    Log.e("FindVM", "updatePassword failed: ${err.errorMessage}")
                    onError(err.errorMessage)
                }
            } catch (e: Exception) {
                Log.e("FindVM", "updatePassword exception", e)
                onError("네트워크 오류: ${e.message}")
            }
        }
    }

    // 7. 이메일 변경
    fun updateEmail(
        newEmail: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val dto = UpdateEmailDto(newEmail)
                val res = RetrofitInstance.getApi(context).updateEmail(dto) // ResponseDto<Unit>
                if (res.isSuccessful) {
                    Log.d("FindVM", "updateEmail success")
                    Toast.makeText(context, "이메일이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    val err = Gson().fromJson(res.errorBody()!!.charStream(), ErrorDto::class.java)
                    Log.e("FindVM", "updateEmail failed: ${err.errorMessage}")
                    onError(err.errorMessage)
                }
            } catch (e: Exception) {
                Log.e("FindVM", "updateEmail exception", e)
                onError("네트워크 오류: ${e.message}")
            }
        }
    }
}

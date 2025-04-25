package com.project.feelrobot.network

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.project.feelrobot.model.dto.ErrorDto
import com.project.feelrobot.model.dto.sign.RefreshDto
import com.project.feelrobot.storage.JwtTokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 재시도 3회 이상이면 중단
        if (responseCount(response) >= 3) return null

        // 저장된 refreshToken 조회
        val refreshToken = runBlocking {
            JwtTokenManager(context).refreshTokenFlow.first()
        } ?: return null

        return runBlocking {
            try {
                // 1) Refresh API 호출
                val api = RetrofitInstance.getApi(context)
                val refreshResponse = api.refreshToken(RefreshDto(refreshToken))

                if (refreshResponse.isSuccessful) {
                    // 2) HTTP 200: RefreshTokenResponseDto 파싱
                    val body = refreshResponse.body()
                    val newAccessToken = body?.accessToken

                    if (!newAccessToken.isNullOrBlank()) {
                        // 3) 토큰 저장
                        JwtTokenManager(context).saveTokens(newAccessToken, refreshToken)
                        Log.d("TokenAuthenticator", "token refresh 완료")

                        // 4) 원래 요청을 새로운 헤더로 재시도
                        return@runBlocking response.request.newBuilder()
                            .header("Authorization", newAccessToken).build()
                    } else {
                        Log.e("TokenAuthenticator", "새 access token이 null 혹은 빈 문자열입니다.")
                        return@runBlocking null
                    }
                } else {
                    // 5) HTTP 400/500: ErrorDto 로 파싱
                    val errJson = refreshResponse.errorBody()?.charStream()
                    val errorDto = Gson().fromJson(errJson, ErrorDto::class.java)
                    Log.e(
                        "TokenAuthenticator",
                        "토큰 리프레시 실패: code=${errorDto.errorCode}, msg=${errorDto.errorMessage}"
                    )
                    return@runBlocking null
                }

            } catch (e: Exception) {
                // 네트워크 오류, JSON 파싱 오류 등
                Log.e("TokenAuthenticator", "토큰 리프레시 중 예외 발생: ${e.message}", e)
                return@runBlocking null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}

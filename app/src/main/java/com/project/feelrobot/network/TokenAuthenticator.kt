package com.project.feelrobot.network

import android.content.Context
import android.util.Log
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
        if (responseCount(response) >= 3) return null

        val refreshToken = runBlocking {
            JwtTokenManager(context).refreshTokenFlow.first()
        } ?: return null

        return runBlocking {
            try {
                // Refresh API 호출
                val refreshResponse = RetrofitInstance.getApi(context).refreshToken(RefreshDto(refreshToken))

                if (refreshResponse.isSuccessful) {
                    // 정상 응답 (HTTP 200)
                    val newAccessToken = refreshResponse.body() ?: ""
                    if (newAccessToken.isNotEmpty()) {
                        // 새 토큰 저장
                        JwtTokenManager(context).saveTokens(newAccessToken, refreshToken)
                        Log.d("TokenAuthenticator", "token refresh 완료")
                        // 원래 요청 재시도
                        response.request.newBuilder()
                            .header("Authorization", newAccessToken)
                            .build()
                    } else {
                        // body()가 null
                        Log.e("TokenAuthenticator", "새로운 access token이 null입니다.")
                        null
                    }
                } else {
                    // 에러 응답
                    val statusCode = refreshResponse.code()
                    val errorBodyStr = refreshResponse.errorBody()?.string() ?: "Unknown error"
                    Log.e("TokenAuthenticator", "토큰 리프레시 실패: $statusCode / $errorBodyStr")
                    null
                }

            } catch (e: Exception) {
                // MalformedJsonException 등 발생
                Log.e("TokenAuthenticator", "토큰 리프레시 중 예외 발생: ${e.message}", e)
                null
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

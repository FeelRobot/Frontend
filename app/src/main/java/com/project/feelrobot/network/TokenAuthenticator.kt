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
        // 무한 재시도 방지
        if (responseCount(response) >= 3) {
            return null
        }
        // 저장된 리프레시 토큰 가져오기
        val refreshToken = runBlocking { JwtTokenManager(context).refreshTokenFlow.first() } ?: return null

        return runBlocking {
            try {
                val refreshResponse = RetrofitInstance.getApi(context).refreshToken(RefreshDto(refreshToken))
                if (refreshResponse.isSuccessful) {
                    val newAccessToken = refreshResponse.body()?.accessToken
                    if (newAccessToken != null) {
                        // 새 토큰 저장
                        JwtTokenManager(context).saveTokens(newAccessToken, refreshToken)
                        // Authorization 헤더에 새로운 토큰 추가하여 원래 요청 재시도
                        response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    } else {
                        Log.e("TokenAuthenticator", "새로운 access token이 null입니다.")
                        null
                    }
                } else {
                    Log.e("TokenAuthenticator", "토큰 리프레시 실패: HTTP ${refreshResponse.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("TokenAuthenticator", "토큰 리프레시 중 예외 발생: ${e.message}", e)
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            result++
            priorResponse = priorResponse.priorResponse
        }
        return result
    }
}

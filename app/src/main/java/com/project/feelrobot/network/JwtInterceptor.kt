package com.project.feelrobot.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.project.feelrobot.storage.JwtTokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor(private val context: Context) : Interceptor {
    @SuppressLint("SuspiciousIndentation")
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            try {
                JwtTokenManager(context).accessTokenFlow.first()
            } catch (e: Exception) {
                null
            }
        }
        Log.d("JwtInterceptor", "추가할 토큰: $token")

        val request = chain.request().newBuilder()
        token?.let {
            request.addHeader("Authorization", it) // JWT 포함, Bearer 제거
        }

        return chain.proceed(request.build())
    }
}

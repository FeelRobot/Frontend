package com.project.feelrobot.network

import android.annotation.SuppressLint
import android.content.Context
import com.project.feelrobot.storage.JwtTokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor(private val context: Context) : Interceptor {
    @SuppressLint("SuspiciousIndentation")
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            JwtTokenManager(context).accessTokenFlow.first()
        }

        val request = chain.request().newBuilder()
            token?.let {
                request.addHeader("Authorization", "Bearer $it") // JWT 포함
            }

        return chain.proceed(request.build())
    }
}

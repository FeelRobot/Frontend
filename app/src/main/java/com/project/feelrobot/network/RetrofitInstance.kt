package com.project.feelrobot.network

import android.content.Context
import android.util.Log
import com.project.feelrobot.BuildConfig
import okhttp3.OkHttpClient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    private var retrofit: Retrofit? = null

    // Retrofit 인스턴스 생성
    val api: Api by lazy {
        createRetrofitInstance()
    }

    // Retrofit 인스턴스를 생성하는 함수 (default)
    private fun createRetrofitInstance(): Api {
        Log.d("RetrofitInstance", "API_BASE_URL = ${BuildConfig.API_BASE_URL}") // 디버깅 로그 추가

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    // JwtInterceptor를 적용한 Retrofit 인스턴스를 반환하는 함수
    fun getApi(context: Context): Api {
        Log.d("RetrofitInstance", "API_BASE_URL = ${BuildConfig.API_BASE_URL}") // 디버깅 로그 추가

        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .addInterceptor(JwtInterceptor(context)) // JwtInterceptor 추가
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // OkHttpClient 적용
                .build()
        }
        return retrofit!!.create(Api::class.java)
    }
}
package com.project.feelrobot.network

import com.project.feelrobot.BuildConfig

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    // Retrofit 인스턴스 생성
    val api: Api by lazy {
        createRetrofitInstance()
    }

    private fun createRetrofitInstance(): Api {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

}
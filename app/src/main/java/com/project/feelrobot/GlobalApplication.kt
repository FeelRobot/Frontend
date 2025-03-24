package com.project.feelrobot

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao Sdk 초기화
        val kakaoKey = getString(R.string.kakao_native_app_key)

        if (kakaoKey.isBlank()) {
            Log.e("GlobalApplication", "Kakao SDK 초기화 실패: 키 값이 비어 있음")
        } else {
            KakaoSdk.init(this, kakaoKey)
            Log.i("GlobalApplication", "Kakao SDK 초기화 성공: $kakaoKey")
        }
    }
}
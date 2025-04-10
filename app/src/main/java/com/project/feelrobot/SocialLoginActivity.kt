package com.project.feelrobot

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.project.feelrobot.storage.JwtTokenManager
import kotlinx.coroutines.launch

class SocialLoginActivity : ComponentActivity() {
    // 딥링크로 전달받은 인가 코드를 저장하는 상태 변수
    private var deepLinkCode by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("SocialLoginActivity", "SocialLoginActivity 실행")

        // 딥링크 인텐트를 먼저 처리하여 인가 코드를 deepLinkCode에 저장
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    // 딥링크에서 인가 코드 또는 토큰 정보를 추출하여 처리
    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri: Uri ->
            Log.d("SocialLoginActivity", "딥링크 URI: $uri")

            val code = uri.getQueryParameter("code")
            val accessToken = uri.getQueryParameter("accessToken")
            val refreshToken = uri.getQueryParameter("refreshToken")
            val email = uri.getQueryParameter("email")

            when {
                // 1) code 존재
                !code.isNullOrEmpty() -> {
                    Log.d("SocialLoginActivity", "Received authorization code: $code")
                    deepLinkCode = code
                    return@let
                }
                // 2) accessToken & refreshToken
                !accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty() -> {
                    Log.d("SocialLoginActivity", "Received tokens: $accessToken / $refreshToken")
                    lifecycleScope.launch {
                        try {
                            JwtTokenManager(this@SocialLoginActivity).apply {
                                saveTokens(accessToken, refreshToken)
                                setAutoLogin(true)
                            }
                            startActivity(Intent(
                                this@SocialLoginActivity, MainActivity::class.java
                            ).apply {
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                            finish()
                        } catch (e: Exception) {
                            Log.e("SocialLoginActivity", "토큰 저장 중 에러: ${e.message}")
                        }
                    }
                }
                // 3) email
                !email.isNullOrEmpty() -> {
                    Log.d("SocialLoginActivity", "Received email: $email")
                    Intent(this@SocialLoginActivity, MainActivity::class.java).apply {
                        putExtra("navigate_to", "signup")
                        putExtra("signup_email", email)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }.also {
                        startActivity(it)
                        finish()
                    }
                }
                // 4) 아무것도 없음
                else -> {
                    Log.d("SocialLoginActivity", "딥링크에 code/accessToken/email 중 아무것도 없음.")
                }
            }
        }
    }
}
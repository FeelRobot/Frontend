package com.project.feelrobot

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.project.feelrobot.ui.screens.LoginScreen
import com.project.feelrobot.ui.theme.FeelRobotTheme
import com.project.feelrobot.viewmodel.LoginViewModel

class SocialLoginActivity : ComponentActivity() {

    // 딥링크로 전달받은 인가 코드를 저장하는 상태 변수
    private var deepLinkCode by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 딥링크 인텐트를 먼저 처리하여 인가 코드를 deepLinkCode에 저장
        handleDeepLink(intent)
        setContent {
            FeelRobotTheme {
                val navController = rememberNavController()
                val loginViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel<LoginViewModel>()

                // LaunchedEffect를 통해 deepLinkCode가 변경되면 백엔드 API 호출
                LaunchedEffect(key1 = deepLinkCode) {
                    deepLinkCode?.let { code ->
                        Log.d("SocialLoginActivity", "Processing authorization code: $code")
                        loginViewModel.kakaoLoginWithAuthCode(
                            code,
                            this@SocialLoginActivity,
                            navController
                        )
                        // 한 번 처리한 후 초기화 (중복 호출 방지)
                        deepLinkCode = null
                    }
                }
                LoginScreen(navController = navController, loginViewModel = loginViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    // 딥링크에서 인가 코드 또는 토큰 정보를 추출하여 처리
    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri: Uri ->
            // 인가 코드(code) parameter가 있는지 확인
            val code = uri.getQueryParameter("code")
            if (!code.isNullOrEmpty()) {
                Log.d("SocialLoginActivity", "Received authorization code: $code")
                deepLinkCode = code
                return
            }
            // 인가 코드가 없으면 토큰 정보가 있는지 확인 (백엔드에서 redirection으로 전달한 경우)
            val accessToken = uri.getQueryParameter("accessToken")
            val refreshToken = uri.getQueryParameter("refreshToken")
            if (!accessToken.isNullOrEmpty()) {
                Log.d(
                    "SocialLoginActivity",
                    "Received tokens from redirect: accessToken=$accessToken, refreshToken=$refreshToken"
                )
                TODO("JwtTokenManager에 토큰 저장")
                TODO("홈으로 이동")
            }
        }
    }
}

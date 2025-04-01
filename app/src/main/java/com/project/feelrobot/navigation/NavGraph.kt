package com.project.feelrobot.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.project.feelrobot.ui.screens.ChatScreen
import com.project.feelrobot.ui.screens.GoogleLoginScreen
import com.project.feelrobot.ui.screens.HomeScreen
import com.project.feelrobot.ui.screens.KakaoLoginScreen
import com.project.feelrobot.ui.screens.MyPageScreen
import com.project.feelrobot.ui.screens.login.LoginScreen
import com.project.feelrobot.ui.screens.login.SignupScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController, startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
        composable("myPage") { MyPageScreen() }
        composable("chat") { ChatScreen() }

        // 로그인 관련 페이지
        composable("login") { LoginScreen(navController) }

        // 카카오 로그인 시 회원가입 미상태이면 이메일을 함께 파라미터로 전달
        composable(
            "signup?email={email}", arguments = listOf(navArgument("email") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            SignupScreen(email, navController)
        }
        composable("kakaoLogin") { KakaoLoginScreen() }
        composable("googleLogin") { GoogleLoginScreen() }

    }
}

package com.project.feelrobot.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.project.feelrobot.ui.screens.ChatScreen
import com.project.feelrobot.ui.screens.HomeScreen
import com.project.feelrobot.ui.screens.LoginScreen
import com.project.feelrobot.ui.screens.MyPageScreen
import com.project.feelrobot.ui.screens.SignupScreen


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
        composable("login") { LoginScreen() }
        composable("signup") { SignupScreen() }

    }
}

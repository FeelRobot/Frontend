package com.project.feelrobot.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.project.feelrobot.model.dto.sign.RegisterDto
import com.project.feelrobot.ui.screens.ChatScreen
import com.project.feelrobot.ui.screens.HomeScreen
import com.project.feelrobot.ui.screens.MyPageScreen
import com.project.feelrobot.ui.screens.updateInfo.UpdateInfoScreen
import com.project.feelrobot.ui.screens.findInfo.FindIdConfirmScreen
import com.project.feelrobot.ui.screens.findInfo.FindIdScreen
import com.project.feelrobot.ui.screens.findInfo.ResetPasswordScreen
import com.project.feelrobot.ui.screens.login.LoginScreen
import com.project.feelrobot.ui.screens.login.SignupScreen
import com.project.feelrobot.ui.screens.login.SurveyScreen
import com.project.feelrobot.ui.screens.updateInfo.ChangeEmailScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController, startDestination = "login"
    ) {
        composable("home") { HomeScreen(navController) }
        composable("myPage") { MyPageScreen(navController) }
        composable("chat") { ChatScreen() }

        // 아이디 찾기 화면
        composable("findId") { FindIdScreen(navController) }
        // 아이디 확인 화면 (Path parameter 사용)
        composable("findIdConfirm/{email}", arguments = listOf(navArgument("email") {
            type = NavType.StringType
        })) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")!!
            FindIdConfirmScreen(email)
        }

        // 비밀번호 재설정 페이지
        composable("resetPassword") { ResetPasswordScreen(navController) }

        // 계정 정보 수정 페이지
        composable("updateInfo") { UpdateInfoScreen(navController) }
        composable("changePassword") { ResetPasswordScreen(navController) }
        composable("changeEmail") { ChangeEmailScreen(navController) }

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

        // 가입한 유저가 학생일 경우 설문조사 페이지 이동
        composable(
            route = "survey?id={id}&password={password}&email={email}&name={name}&role={role}",
            arguments = listOf(navArgument("id") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("role") { type = NavType.IntType })
        ) { backStackEntry ->
            val idArg = backStackEntry.arguments?.getString("id") ?: ""
            val passwordArg = backStackEntry.arguments?.getString("password") ?: ""
            val emailArg = backStackEntry.arguments?.getString("email") ?: ""
            val nameArg = backStackEntry.arguments?.getString("name") ?: ""
            val roleArg = backStackEntry.arguments?.getInt("role") ?: 0

            val registerDto = RegisterDto(idArg, passwordArg, emailArg, nameArg, roleArg)

            SurveyScreen(navController, registerDto)
        }
    }
}
package com.project.feelrobot

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.feelrobot.components.Sidebar
import com.project.feelrobot.components.UnifiedTopBar
import com.project.feelrobot.navigation.NavGraph
import com.project.feelrobot.ui.theme.FeelRobotTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var navigateTo: String? = null
    private var signupEmail: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 인텐트에서 값 추출
        navigateTo = intent.getStringExtra("navigate_to")
        signupEmail = intent.getStringExtra("signup_email")

        setContent {
            FeelRobotTheme {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    when (navigateTo) {
                        "signup" -> {
                            // 회원가입 화면으로 이동, email이 있다면 파라미터로 전달
                            signupEmail?.let { email ->
                                val route = "signup?email=$email"
                                navController.navigate(route) {
                                    popUpTo("login") { inclusive = true }
                                }
                            } ?: run {
                                // email이 없는 경우 그냥 signup 으로
                                navController.navigate("signup") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }

                        else -> {
                            // 아무것도 안 하면 NavGraph의 startDestination으로 이동
                        }
                    }
                }
                MainScreen(
                    navController = navController
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController
) {
    // 사이드바의 초기 상태 설정 (닫힌 상태)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope() // 코루틴 스코프 생성

    ModalNavigationDrawer(drawerState = drawerState, // 사이드바 상태
        drawerContent = {
            Sidebar(navController = navController, drawerState = drawerState) // 사이드바 composable
        }) {
        Scaffold(topBar = {
            UnifiedTopBar(navController) {
                scope.launch {
                    drawerState.open() // 메뉴 버튼 클릭 시 사이드바 열기
                }
            }
        }, content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Log.d("MainScreen", "Scaffold: NavGraph 설정 시작")
                NavGraph(
                    navController = navController
                )
            }
        })
    }
}
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.feelrobot.components.Sidebar
import com.project.feelrobot.navigation.NavGraph
import com.project.feelrobot.ui.theme.FeelRobotTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeelRobotTheme {
                val navController = rememberNavController()

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
            TopAppBar(title = { Text("FeelRobot") }, // App Bar 제목 설정
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.open() // 메뉴 버튼 클릭 시 사이드바 열기
                        }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu") // 햄버거 메뉴 아이콘
                    }
                })
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
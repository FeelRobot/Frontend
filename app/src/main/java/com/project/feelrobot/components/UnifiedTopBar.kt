package com.project.feelrobot.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedTopBar(navController: NavHostController, onMenuClick: () -> Unit) {
    // 현재 NavBackStackEntry를 observe하여 현재 destination을 알아냄
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteFull = currentBackStackEntry?.destination?.route ?: ""
    val currentRoute = currentRouteFull.substringBefore("?")

    // 현재 route에 따른 제목을 매핑
    val title = when (currentRoute) {
        "home" -> "홈"
        "myPage" -> "마이페이지"
        "login" -> "로그인"
        "signup" -> "회원가입"
        else -> "Feelobot"
    }

    // 하나의 TopAppBar에서 제목 표시
    TopAppBar(title = { Text(text = title) }, navigationIcon = {
        // 예시로 아이콘 버튼 추가
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Default.Menu, contentDescription = "Menu"
            )
        }
    }, colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.White
    )
    )
}

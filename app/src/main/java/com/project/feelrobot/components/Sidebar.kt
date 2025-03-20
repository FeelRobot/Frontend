package com.project.feelrobot.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// 사이드바 안의 개별 버튼 구성
@Composable
fun SidebarButton(
    label: String, // 버튼 텍스트
    navController: NavController, // 네비게이션 컨트롤러
    route: String, // 이동할 네이게이션 경로
    drawerState: DrawerState, // DrawerState 객체
    scope: CoroutineScope, // 코루틴 스코프
    icon: @Composable (() -> Unit)? = null // 아이콘 Composable을 선택적으로 전달
) {
    Button(
        onClick = {
            // 버튼 클릭 시 동작
            scope.launch {
                drawerState.close() // 버튼 클릭 시 사이드바를 닫음
            }
            navController.navigate(route) // 지정된 네비게이션 경로로 이동
        }, modifier = Modifier
            .fillMaxWidth() // 버튼이 사이드바의 가로를 완전히 채우도록 설정
            .height(48.dp) // 버튼의 높이를 고정하여 균일한 크기 유지
            .shadow(6.dp), // 🔥 그림자 추가
        shape = Shapes().small.copy(CornerSize(0.dp)) // 버튼 모서리를 직각으로 설정
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), // 아이콘과 텍스트를 가로로 배치
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically // 세로 중앙 정렬
        ) {
            if (icon != null) {
                Box(modifier = Modifier.padding(end = 8.dp)) { // 텍스트와의 간격 설정
                    icon() // 아이콘 표시
                }
            }
            Text(text = label) // 버튼에 텍스트 표시
        }
    }
}

// Sidebar: 사이드바 구성
@Composable
fun Sidebar(navController: NavController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope() // 코루틴을 사용하여 비동기 동작 제어

    // 사이드바의 전체 레이아웃
    Box(
        modifier = Modifier
            .fillMaxHeight() // 화면의 세로를 모두 채우도록 설정
            .width(270.dp) // 사이드바의 가로 크기를 270dp로 고정
            .background(color = MaterialTheme.colorScheme.primaryContainer) // 사이드바의 배경색을 연회색으로 설정
            .padding(vertical = 16.dp) // 내부 수직 패딩 추가
    ) {
        // 버튼을 세로로 배치하기 위한 Column
        Column(
            modifier = Modifier.padding(vertical = 16.dp) // 버튼 간 여유 공간
        ) {
            // 각각의 버튼을 호출하여 사이드바에 추가
            SidebarButton("홈", navController, "home", drawerState, scope) {
                Icon(Icons.Default.Home, contentDescription = "Home Icon") // 아이콘 추가
            }
            SidebarButton("내 정보", navController, "myPage", drawerState, scope){
                Icon(Icons.Default.Person, contentDescription = "My page")
            }
            SidebarButton("회원가입", navController, "signup", drawerState, scope) {

            }
            SidebarButton("로그인", navController, "login", drawerState, scope) {

            }
        }
    }
}

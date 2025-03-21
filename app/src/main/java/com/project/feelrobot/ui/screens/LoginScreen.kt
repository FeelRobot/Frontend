package com.project.feelrobot.ui.screens

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.feelrobot.R
import com.project.feelrobot.components.TextFieldRow
import com.project.feelrobot.storage.JwtTokenManager
import com.project.feelrobot.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAutoLogin by remember { mutableStateOf(false) } // 자동 로그인 체크박스 상태

    // 저장된 자동 로그인 정보 불러오기
    LaunchedEffect(Unit) {
        val tokenManager = JwtTokenManager(context)
        tokenManager.isAutoLoginFlow.collectLatest { savedAutoLogin ->
            isAutoLogin = savedAutoLogin
            if (isAutoLogin) {
                tokenManager.accessTokenFlow.collectLatest { token ->
                    if (!token.isNullOrEmpty()) {
                        println("자동 로그인: 토큰이 존재하여 로그인 유지")
                        // 자동 로그인 처리: 토큰이 유효하면 홈 화면으로 이동
                        navController.navigate("home") { popUpTo("login") { inclusive = true } }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.feelrobot_login),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(), // 전체 크기 채우기
            contentScale = ContentScale.Crop // 화면을 꽉 채우도록 조정
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 중앙 로고 배치
            Image(
                painter = painterResource(id = R.drawable.feelrobot_icon), // 이미지 리소스
                contentDescription = "FeelRobot Icon",
                modifier = Modifier
                    .padding(top = 100.dp)
                    .height(200.dp),
            )

            Spacer(modifier = Modifier.height(100.dp))

            // 로그인 입력폼
            LoginForm(id = id,
                password = password,
                onIdChange = { id = it },
                onPasswordChange = { password = it })

            // 자동 로그인 체크박스 추가
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Checkbox(
                    checked = isAutoLogin, onCheckedChange = { isAutoLogin = it }, // 상태 업데이트
                    colors = androidx.compose.material3.CheckboxDefaults.colors(
                        checkedColor = Color(0xFF1A237E)
                    )
                )
                Text(
                    text = "자동 로그인",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 로그인 버튼
            Button(
                onClick = {
                    coroutineScope.launch {
                        loginViewModel.login(id, password, isAutoLogin, context, navController)
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(50.dp)
            ) {
                Text(text = "로그인", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 아이디 찾기, 비밀번호 찾기, 회원가입 네비게이션
            Row {
                Text(text = "아이디 찾기",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clickable { navController.navigate("findId") }
                        .padding(horizontal = 8.dp))

                Text(text = "|", fontSize = 14.sp, color = Color.White)

                Text(text = "비밀번호 찾기",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clickable { navController.navigate("findPassword") }
                        .padding(horizontal = 8.dp))

                Text(text = "|", fontSize = 14.sp, color = Color.White)

                Text(text = "회원가입",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clickable { navController.navigate("signup") }
                        .padding(horizontal = 8.dp))
            }
        }
    }
}

@Composable
fun LoginForm(
    id: String, password: String, onIdChange: (String) -> Unit, onPasswordChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(0.85f)) {
        TextFieldRow(
            label = "아이디",
            value = id,
            placeholder = "아이디를 입력하세요.",
            onValueChange = onIdChange,
            fontColor = Color.White
        )

        TextFieldRow(
            label = "비밀번호",
            value = password,
            placeholder = "비밀번호를 입력하세요.",
            onValueChange = onPasswordChange,
            isPassword = true,
            fontColor = Color.White
        )
    }
}
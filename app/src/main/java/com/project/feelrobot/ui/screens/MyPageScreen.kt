package com.project.feelrobot.ui.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.feelrobot.viewmodel.UserInfoState
import com.project.feelrobot.viewmodel.UserInfoViewModel
import com.project.feelrobot.viewmodel.UserInfoViewModelFactory

@Composable
fun MyPageScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val userInfoViewModel: UserInfoViewModel = viewModel(
        factory = UserInfoViewModelFactory(context)
    )

    val uiState by userInfoViewModel.userInfoState.collectAsState()

    LaunchedEffect(Unit) {
        userInfoViewModel.fetchUserInfo()
    }

    when (uiState) {
        is UserInfoState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UserInfoState.Error -> {
            val errMsg = (uiState as UserInfoState.Error).message
            Log.d("MyPageScreen", "오류 발생 시도: $errMsg")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "오류 발생: $errMsg")
            }
        }

        is UserInfoState.Student -> {
            val data = (uiState as UserInfoState.Student).data
            MyPageContent(
                navController, role = "학생", infoItems = listOf(
                    "아이디" to data.userId,
                    "생년월일" to data.birth,
                    "이메일" to data.email,
                    "성별" to if (data.sex == 0) "남" else "여",
                    "이름" to data.name,
                    "보호자 아이디" to data.managerId,
                    "비밀번호" to "*******"
                )
            )
        }

        is UserInfoState.Manager -> {
            val data = (uiState as UserInfoState.Manager).data
            MyPageContent(
                navController, role = "보호자", infoItems = listOf(
                    "아이디" to data.userId,
                    "이메일" to data.email,
                    "이름" to data.name,
                    "비밀번호" to "*******"
                )
            )
        }
    }
}


@Composable
fun MyPageContent(
    navController: NavController,
    role: String,
    infoItems: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 프로필
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "프로필 이미지",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = role, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 정보 카드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                infoItems.forEach { (title, value) ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("updateInfo") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF303F9F))
        ) {
            Text(text = "정보 수정하기", color = Color.White)
        }
    }
}

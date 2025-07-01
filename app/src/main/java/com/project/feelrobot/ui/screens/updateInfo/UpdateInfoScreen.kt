package com.project.feelrobot.ui.screens.updateInfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.feelrobot.components.TextFieldRow
import com.project.feelrobot.viewmodel.UserInfoState
import com.project.feelrobot.viewmodel.UserInfoViewModel
import com.project.feelrobot.viewmodel.UserInfoViewModelFactory

@Composable
fun UpdateInfoScreen(
    navController: NavController,
    viewModel: UserInfoViewModel = viewModel(factory = UserInfoViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current

    // 1) 사용자 정보 로드
    val uiState by viewModel.userInfoState.collectAsState()
    LaunchedEffect(Unit) { viewModel.fetchUserInfo() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "정보 수정하기", fontSize = 20.sp, fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))

        when (uiState) {
            is UserInfoState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is UserInfoState.Error -> {
                Text(
                    text = (uiState as UserInfoState.Error).message, color = Color.Red
                )
            }

            is UserInfoState.Student -> {
                val data = (uiState as UserInfoState.Student).data
                EditableFields(role = "학생",
                    infoItems = listOf(
                        "아이디" to data.userId,
                        "이름" to data.name,
                        // 비밀번호와 이메일만 수정 가능
                        "비밀번호" to "********",
                        "이메일" to data.email,
                        "생년월일" to data.birth,
                        "성별" to if (data.sex == 0) "남" else "여",
                        "보호자 아이디" to data.managerId
                    ),
                    onPasswordClick = { navController.navigate("changePassword") },
                    onEmailClick = { navController.navigate("changeEmail") })
            }

            is UserInfoState.Manager -> {
                val data = (uiState as UserInfoState.Manager).data
                EditableFields(role = "보호자",
                    infoItems = listOf(
                        "아이디" to data.userId,
                        "이름" to data.name,
                        "비밀번호" to "********",
                        "이메일" to data.email
                    ),
                    onPasswordClick = { navController.navigate("changePassword") },
                    onEmailClick = { navController.navigate("changeEmail") })
            }
        }
    }
}

@Composable
private fun EditableFields(
    role: String,
    infoItems: List<Pair<String, String>>,
    onPasswordClick: () -> Unit,
    onEmailClick: () -> Unit
) {
    Text(text = "$role 계정 정보", fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(16.dp))

    infoItems.forEach { (label, value) ->
        when (label) {
            "비밀번호" -> {
                TextFieldRow(
                    label = label,
                    value = value,
                    placeholder = "",
                    onValueChange = {},
                    isPassword = true,
                    modifiable = false,
                    buttonText = "수정하기",
                    onButtonClick = onPasswordClick
                )
            }

            "이메일" -> {
                TextFieldRow(
                    label = label,
                    value = value,
                    placeholder = "",
                    onValueChange = {},
                    modifiable = false,
                    buttonText = "수정하기",
                    onButtonClick = onEmailClick
                )
            }

            else -> {
                TextFieldRow(
                    label = label,
                    value = value,
                    placeholder = "",
                    onValueChange = {},
                    modifiable = false
                )
            }
        }
    }
}

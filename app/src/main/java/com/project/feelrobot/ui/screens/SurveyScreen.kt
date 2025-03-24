package com.project.feelrobot.ui.screens

//noinspection UsingMaterialAndMaterial3Libraries
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.feelrobot.R
import com.project.feelrobot.components.TextFieldRow
import com.project.feelrobot.model.dto.RegisterDto
import com.project.feelrobot.viewmodel.SignupViewModel

@Composable
fun SurveyScreen(navController: NavController, signupViewModel: SignupViewModel = viewModel()) {
    val context = LocalContext.current  // 여기서 context를 가져옴

    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var selectedUserType by remember { mutableIntStateOf(0) } // 0: 학생, 1: 보호자
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 상단 이미지 배치
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.feelrobot), // 이미지 리소스
                contentDescription = "Signup Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),

                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 회원가입 텍스트
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "‘필로봇’ 에 오신 걸",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "환영합니다!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black
            )
            Text(
                text = "당신을 설명해주세요!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 입력 필드 폼
        SurveyForm(id = id,
            name = name,
            password = password,
            confirmPassword = confirmPassword,
            email = email,
            onIdChange = { id = it },
            onNameChange = { name = it },
            onPasswordChange = { password = it },
            onConfirmPasswordChange = { confirmPassword = it },
            onEmailChange = { email = it })

        Spacer(modifier = Modifier.height(20.dp))

        UserTypeSelector(selectedUserType = selectedUserType) { selectedUserType = it }

        // 제출 버튼
        Button(
            onClick = {
                if (password != confirmPassword) {
                    Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    signupViewModel.registerUser(
                        RegisterDto(id, password, email, name, selectedUserType), context
                    ) {
                        navController.navigate("login") { popUpTo("signup") { inclusive = true } }
                    }
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10298F) // 버튼 색상 적용
            )
        ) {
            Text(text = "제출", fontSize = 18.sp)
        }
    }
}

@Composable
fun SurveyForm(
    id: String,
    name: String,
    password: String,
    confirmPassword: String,
    email: String,
    onIdChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
) {
    var passwordError by remember { mutableStateOf(false) }  // 비밀번호 검증 상태

    Column(modifier = Modifier.fillMaxWidth(0.85f)) {
        TextFieldRow(label = "아이디",
            value = id,
            placeholder = "아이디를 입력하세요.",
            onValueChange = onIdChange,
            buttonText = "중복 확인",
            onButtonClick = { TODO("아이디 중복 확인 로직 추가") })

        TextFieldRow(
            label = "이름", value = name, placeholder = "이름을 입력하세요.", onValueChange = onNameChange
        )

        TextFieldRow(
            label = "비밀번호",
            value = password,
            placeholder = "비밀번호를 입력하세요.",
            onValueChange = onPasswordChange,
            isPassword = true
        )
        TextFieldRow(
            label = "비밀번호 재입력",
            value = confirmPassword,
            placeholder = "비밀번호를 다시 한번 입력하세요.",
            onValueChange = {
                onConfirmPasswordChange(it)
                passwordError = it.isNotEmpty() && (password != it) // 비밀번호 불일치 시 에러 상태 업데이트
            },
            isPassword = true
        )

        // 비밀번호 불일치 시 에러 메시지 표시
        if (passwordError) {
            Text(
                text = "비밀번호가 일치하지 않습니다.",
                fontSize = 14.sp,
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }

        TextFieldRow(label = "이메일",
            value = email,
            placeholder = "이메일을 입력하세요.",
            buttonText = "인증하기",
            onValueChange = onEmailChange,
            onButtonClick = { TODO("이메일 인증 로직 추가") })
    }
}
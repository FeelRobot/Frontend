package com.project.feelrobot.ui.screens

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.feelrobot.R

@Composable
fun SignupScreen() {
    var selectedUserType by remember { mutableStateOf("학생") }
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
        SignupForm()

        Spacer(modifier = Modifier.height(20.dp))

        UserTypeSelector(selectedUserType = selectedUserType) { selectedUserType = it }

        // 제출 버튼
        Button(
            onClick = { TODO("회원가입 처리") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp)
        ) {
            Text(text = "제출", fontSize = 18.sp)
        }
    }
}

@Composable
fun SignupForm() {
    Column(modifier = Modifier.fillMaxWidth(0.85f)) {
        SignupTextField(label = "아이디", value = "아이디를 입력하세요.")
        SignupTextField(label = "이름", value = "이름을 입력하세요.")
        SignupTextField(label = "비밀번호", value = "비밀번호를 입력하세요.", isPassword = true)
        SignupTextField(label = "비밀번호 재입력", value = "비밀번호를 다시 한번 입력하세요.", isPassword = true)
        SignupTextField(label = "이메일", value = "이메일을 입력하세요.")
    }
}

@Composable
fun SignupTextField(label: String, value: String, isPassword: Boolean = false) {
    var text by remember { mutableStateOf("") }
    var isUserInput by remember { mutableStateOf(false) } // 사용자가 입력했는지 여부

    Column {
        // 라벨
        Text(text = label, fontSize = 14.sp, color = Color.Black)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp) // 내부 패딩 조정
                .height(24.dp), contentAlignment = Alignment.CenterStart // 플레이스홀더 및 입력 텍스트 정렬

        ) {
            // 플레이스홀더 텍스트 (입력하기 전 회색 안내 문구)
            if (!isUserInput) {
                Text(
                    text = value, // 기본값을 안내 문구로 사용
                    fontSize = 14.sp, color = Color.Gray
                )
            }

            // 실제 입력 필드
            BasicTextField(
                value = text,
                onValueChange = {
                    text = it
                    isUserInput = text.isNotEmpty() // 입력 여부 확인
                },
                visualTransformation = if (isPassword && isUserInput) PasswordVisualTransformation() else VisualTransformation.None,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.Black, // 입력하면 항상 검은색으로
                    fontSize = 14.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
@Composable
fun UserTypeSelector(selectedUserType: String, onUserTypeSelected: (String) -> Unit) {
    val userTypes = listOf("학생", "보호자")
    Row(
        modifier = Modifier.fillMaxWidth(0.85f),
        horizontalArrangement = Arrangement.Center
    ) {
        userTypes.forEach { userType ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onUserTypeSelected(userType) } // 선택 시 변경
            ) {
                RadioButton(
                    selected = (selectedUserType == userType),
                    onClick = { onUserTypeSelected(userType) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1A237E)) // 선택된 색상 (남색 계열)
                )
                Text(
                    text = userType,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

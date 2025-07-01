package com.project.feelrobot.ui.screens.updateInfo

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.feelrobot.components.TextFieldRow
import com.project.feelrobot.viewmodel.FindViewModel

@Composable
fun ChangeEmailScreen(
    navController: NavController, viewModel: FindViewModel = viewModel()
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var step by remember { mutableIntStateOf(0) }   // 0=메일 전송, 1=코드 확인
    var isSending by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("이메일 변경", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        // 1) 이메일 입력 + 인증버튼
        if (step == 0) {
            TextFieldRow(label = "이메일",
                value = email,
                placeholder = "새 이메일을 입력하세요",
                onValueChange = { email = it },
                buttonText = if (!isSending) "인증하기" else "전송중...",
                onButtonClick = {
                    isSending = true
                    viewModel.sendEmailAuth(email, context, onNext = {
                        isSending = false
                        step = 1
                        Toast.makeText(context, "인증번호가 발송되었습니다.", Toast.LENGTH_SHORT).show()
                    }, onError = {
                        isSending = false
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    })
                })
        }

        // 2) 인증번호 입력 + 확인버튼
        if (step == 1) {
            TextFieldRow(label = "인증번호",
                value = code,
                placeholder = "메일로 받은 번호 입력",
                onValueChange = { code = it },
                buttonText = if (!isVerifying) "확인" else "검증중...",
                onButtonClick = {
                    isVerifying = true
                    viewModel.sendEmailAuth(email, context, onNext = {
                        isVerifying = false
                        viewModel.updateEmail(email, context, onSuccess = {
                            Toast.makeText(context, "이메일이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()  // 이전 화면으로
                        }, onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        })
                    }, onError = {
                        isVerifying = false
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    })
                })
        }
    }
}

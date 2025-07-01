package com.project.feelrobot.ui.screens.findInfo

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.feelrobot.components.TextFieldRow
import com.project.feelrobot.viewmodel.FindViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavController, viewModel: FindViewModel = viewModel()
) {
    val context = LocalContext.current

    var step by remember { mutableIntStateOf(0) }         // 0=ID+이메일 검증, 1=코드 확인, 2=비밀번호 입력
    var id by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPwd by remember { mutableStateOf("") }
    var confirmPwd by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("비밀번호 재설정", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        when (step) {
            0 -> {
                // ID + 이메일 검증 및 메일 전송
                TextFieldRow(label = "아이디",
                    value = id,
                    placeholder = "아이디 입력",
                    onValueChange = { id = it })
                TextFieldRow(label = "이메일",
                    value = email,
                    placeholder = "aaa@aaa.com",
                    onValueChange = { email = it},
                    modifiable = true,
                    buttonText = "인증하기",
                    onButtonClick = {
                        viewModel.requestPasswordReset(id, email, context, onNext = {
                            step = 1; Toast.makeText(
                            context, "인증번호 발송됨", Toast.LENGTH_SHORT
                        ).show()
                        }, onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
                    })
            }

            1 -> {
                // 코드 확인
                TextFieldRow(label = "인증번호",
                    value = code,
                    placeholder = "메일로 받은 번호",
                    onValueChange = { code = it },
                    buttonText = "확인",
                    onButtonClick = {
                        viewModel.verifyPasswordCode(email,
                            code,
                            context,
                            onNext = { step = 2 },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
                    })
            }

            2 -> {
                // 새 비밀번호 입력
                TextFieldRow(
                    label = "비밀번호",
                    value = newPwd,
                    placeholder = "새 비밀번호",
                    onValueChange = { newPwd = it },
                    isPassword = true
                )
                TextFieldRow(
                    label = "비밀번호 재입력",
                    value = confirmPwd,
                    placeholder = "다시 입력",
                    onValueChange = { confirmPwd = it },
                    isPassword = true
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (newPwd != confirmPwd || newPwd.isBlank()) {
                            Toast.makeText(context, "비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.updatePassword(id, newPwd, context, onSuccess = {
                                Toast.makeText(context, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()  // 이전 화면으로
                            }, onError = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10298F))
                ) {
                    Text("비밀번호 재설정", color = Color.White)
                }
            }
        }
    }
}

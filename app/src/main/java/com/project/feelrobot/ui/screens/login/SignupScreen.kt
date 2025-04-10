package com.project.feelrobot.ui.screens.login

//noinspection UsingMaterialAndMaterial3Libraries
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.project.feelrobot.model.dto.sign.RegisterDto
import com.project.feelrobot.viewmodel.SignupViewModel

@Composable
fun SignupScreen(
    initialEmail: String,
    navController: NavController,
    signupViewModel: SignupViewModel = viewModel()
) {
    val context = LocalContext.current
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var selectedUserType by remember { mutableIntStateOf(0) } // 0: 학생, 1: 보호자
    val scrollState = rememberScrollState()
    var isSignPossible by remember { mutableStateOf(false) } // 회원가입 가능 여부

    val isSocialLogin = initialEmail.isNotEmpty() // 소셜로그인 여부

    // 소셜 로그인 시 id, email 고정, 비밀번호 랜덤 문자열 설정
    LaunchedEffect(Unit) {
        if (isSocialLogin) {
            id = initialEmail
            email = initialEmail
            password = "******"
            confirmPassword = "******"
        }
    }

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
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 입력 필드 폼
        SignupForm(id = id,
            name = name,
            password = password,
            confirmPassword = confirmPassword,
            email = email,
            onIdChange = { id = it },
            onNameChange = { name = it },
            onPasswordChange = { password = it },
            onConfirmPasswordChange = { confirmPassword = it },
            onEmailChange = { email = it },
            signupViewModel = signupViewModel, // ViewModel 주입
            isSocialLogin = isSocialLogin,
            onSignPossibleChange = { new -> isSignPossible = new })

        Spacer(modifier = Modifier.height(20.dp))

        UserTypeSelector(selectedUserType = selectedUserType) { selectedUserType = it }

        // 제출 버튼
        Button(
            onClick = {
                if (!isSignPossible) {
                    Toast.makeText(context, "필수 항목을 모두 충족해야 합니다.", Toast.LENGTH_SHORT).show()
                    return@Button
                } else {
                    if (selectedUserType == 0) { // 학생이면 설문조사 페이지로 이동
                        val route =
                            "survey?id=${id}" + "&password=${password}" + "&email=${email}" + "&name=${name}" + "&role=${0}"

                        navController.navigate(route) {
                            popUpTo("signup") { inclusive = true }
                        }
                    } else { // 보호자면 바로 register api를 호출하고 home으로 이동
                        signupViewModel.registerUser(RegisterDto(
                            id, password, email, name, selectedUserType
                        ), context, onSuccess = {
                            // 최종 가입 + 로그인 성공 -> 홈 이동
                            navController.navigate("home") {
                                popUpTo("signup") { inclusive = true }
                            }
                        }, onFailure = { errMsg ->
                            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                        })
                    }
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10298F) // 버튼 색상 적용
            ),
            enabled = isSignPossible
        ) {
            Text(text = "제출", fontSize = 18.sp)
        }
    }
}

@Composable
fun SignupForm(
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
    signupViewModel: SignupViewModel,
    isSocialLogin: Boolean,
    onSignPossibleChange: (Boolean) -> Unit
) {
    var passwordError by remember { mutableStateOf(false) }  // 비밀번호 검증 상태
    var emailCode by remember { mutableStateOf("") }         // 이메일 인증번호 입력 필드
    var isIdDuplicated by remember { mutableStateOf(true) } // 아이디 중복 여부
    var idCheckDone by remember { mutableStateOf(false) } // 아이디 중복 체크 수행 여부
    var emailVerifyDone by remember { mutableStateOf(false) } // 이메일 인증 수행 여부
    var isEmailVerified by remember { mutableStateOf(false) } // 인증 성공 여부
    var isNameEmpty by remember { mutableStateOf(true) } // 이름 입력 여부

    val context = LocalContext.current

    // 소셜로그인 시 isIdDuplicated = false, passwordError = false, isEmailVerified = true 로 고정
    LaunchedEffect(isSocialLogin) {
        if (isSocialLogin) {
            idCheckDone = true
            checkSignPossible(
                true,
                isIdDuplicated = false,
                isNameEmpty,
                passwordError = false,
                isEmailVerified = true,
                onSignPossibleChange
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth(0.85f)) {
        // 1. 아이디
        TextFieldRow(label = "아이디",
            value = id,
            placeholder = "아이디를 입력하세요.",
            onValueChange = onIdChange,
            modifiable = !isSocialLogin,
            buttonText = if (!isSocialLogin) "중복 확인" else null,
            onButtonClick = {
                signupViewModel.checkIdDuplication(id, context) { success ->
                    idCheckDone = true
                    isIdDuplicated = !success // 아이디 사용 가능

                    // 바뀐 상태에 따라 가입 가능 여부 재계산
                    checkSignPossible(
                        isSocialLogin,
                        isIdDuplicated,
                        isNameEmpty,
                        passwordError,
                        isEmailVerified,
                        onSignPossibleChange
                    )
                }
            })
        if (idCheckDone && isIdDuplicated && !isSocialLogin) {
            Text(
                text = "이미 사용 중인 아이디입니다.",
                fontSize = 14.sp,
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }

        // 2. 이름
        TextFieldRow(label = "이름", value = name, placeholder = "이름을 입력하세요.", onValueChange = {
            onNameChange(it)
            isNameEmpty = it.isBlank()
            // 바뀐 상태에 따라 가입 가능 여부 재계산
            checkSignPossible(
                isSocialLogin,
                isIdDuplicated,
                isNameEmpty,
                passwordError,
                isEmailVerified,
                onSignPossibleChange
            )
        })

        // 3. 비밀번호 입력/재입력
        if (!isSocialLogin) {
            TextFieldRow(
                label = "비밀번호",
                value = password,
                placeholder = "비밀번호를 입력하세요.",
                onValueChange = onPasswordChange,
                isPassword = true,
            )
            TextFieldRow(
                label = "비밀번호 재입력",
                value = confirmPassword,
                placeholder = "비밀번호를 다시 한번 입력하세요.",
                onValueChange = {
                    onConfirmPasswordChange(it)
                    passwordError = it.isNotEmpty() && (password != it) // 비밀번호 불일치 시 에러 상태 업데이트
                    checkSignPossible(
                        false,
                        isIdDuplicated,
                        isNameEmpty,
                        passwordError,
                        isEmailVerified,
                        onSignPossibleChange
                    )
                },
                isPassword = true,
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
        }


        // 4. 이메일
        TextFieldRow(label = "이메일",
            value = email,
            placeholder = "이메일을 입력하세요.",
            buttonText = if (!isSocialLogin) "인증하기" else null,
            onValueChange = onEmailChange,
            modifiable = !isSocialLogin, // 소셜 로그인인 경우 false
            onButtonClick = {
                signupViewModel.sendEmailAuth(email, context) { success ->
                    if (success) {
                        // 메일 전송 성공
                    }
                }
            })

        // 5. 인증번호
        if (!isSocialLogin) {
            // 이메일 인증번호 입력 + 확인 버튼
            TextFieldRow(label = "인증번호",
                value = emailCode,
                placeholder = "메일로 받은 번호 입력",
                onValueChange = { emailCode = it },
                buttonText = "인증 확인",
                onButtonClick = {
                    emailVerifyDone = true
                    signupViewModel.verifyEmailAuth(
                        email, emailCode, context
                    ) { verified ->
                        isEmailVerified = verified
                        checkSignPossible(
                            false,
                            isIdDuplicated,
                            isNameEmpty,
                            passwordError,
                            isEmailVerified,
                            onSignPossibleChange
                        )
                    }
                })

            if (emailVerifyDone) {
                if (isEmailVerified) {
                    Text(
                        text = "이메일 인증 성공",
                        fontSize = 14.sp,
                        color = Color.Green,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                } else Text(
                    text = "이메일 인증 실패",
                    fontSize = 14.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun UserTypeSelector(selectedUserType: Int, onUserTypeSelected: (Int) -> Unit) {
    val userTypes = mapOf(0 to "학생", 1 to "보호자") // 0: 학생, 1: 보호자
    Row(
        modifier = Modifier.fillMaxWidth(0.85f), horizontalArrangement = Arrangement.Center
    ) {
        userTypes.forEach { (type, label) ->
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onUserTypeSelected(type) } // 선택 시 변경
            ) {
                RadioButton(
                    selected = (selectedUserType == type),
                    onClick = { onUserTypeSelected(type) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF10298F))
                )
                Text(
                    text = label,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

// 아이디 중복, 비밀번호 확인, 이메일 인증을 통해 가입 가능여부를 판단하기 위해 isSignPossible 변경
private fun checkSignPossible(
    isSocialLogin: Boolean,
    isIdDuplicated: Boolean,
    isNameEmpty: Boolean,
    passwordError: Boolean,
    isEmailVerified: Boolean,
    onSignPossibleChange: (Boolean) -> Unit
) {
    Log.d(
        "SignupScreen",
        "isIdDuplicated: $isIdDuplicated, isNameEmpty: $isNameEmpty, passwordError: $passwordError, isEmailVerified: $isEmailVerified"
    )

    val canSign: Boolean = if (isSocialLogin) {
        !isNameEmpty

    } else {
        !isIdDuplicated && !isNameEmpty && !passwordError && isEmailVerified
    }
    onSignPossibleChange(canSign)
    Log.d("SignupScreen", "canSign: $canSign")

}
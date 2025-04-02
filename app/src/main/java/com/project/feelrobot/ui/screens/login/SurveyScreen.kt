package com.project.feelrobot.ui.screens.login

//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.project.feelrobot.components.BirthdaySelectorRow
import com.project.feelrobot.components.GenderSelectionRow
import com.project.feelrobot.components.TextFieldRow
import com.project.feelrobot.model.dto.sign.RegisterDto
import com.project.feelrobot.model.dto.user.SurveyResponseDto
import com.project.feelrobot.viewmodel.SignupViewModel

@Composable
fun SurveyScreen(
    navController: NavController,
    registerDto: RegisterDto? = null,
    signupViewModel: SignupViewModel = viewModel()
) {
    val context = LocalContext.current  // 여기서 context를 가져옴

    val scrollState = rememberScrollState()
    var birth by remember { mutableStateOf("") } // YYYY-MM-DD 형식
    var sex by remember { mutableIntStateOf(-1) } // -1: 미선택, 0: 남자, 1: 여자
    var managerId by remember { mutableStateOf("") } // 보호자 id

    var isSubmitPossible by remember { mutableStateOf(false) } // 제출 가능 여부

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

        // 설문조사 텍스트
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

        SurveyForm(sex = sex,
            managerId = managerId,
            onBirthChange = { newBirth -> birth = newBirth },
            onSexChange = { newSex -> sex = newSex },
            onManagerIdChange = { newId -> managerId = newId },
            onSubmitPossibleChange = { new -> isSubmitPossible = new })

        Spacer(modifier = Modifier.height(20.dp))

        // 제출 버튼
        Button(
            onClick = {
                if (!isSubmitPossible) {
                    Toast.makeText(context, "필수 항목을 모두 충족해야 합니다.", Toast.LENGTH_SHORT).show()
                    return@Button
                } else { // 설문조사까지 완료하면 회원가입 폼과 설문조사 폼을 함께 백으로 전송
                    if (registerDto != null) {
                        val surveyResponseDto =
                            SurveyResponseDto(registerDto.id, birth, sex, managerId)

                        signupViewModel.registerUser(
                            registerDto, context
                        ) {

                            signupViewModel.submitSurvey(surveyResponseDto, context, onSuccess = {
                                // 성공 시 화면 이동
                                navController.navigate("home") {
                                    popUpTo("survey") {
                                        inclusive = true
                                    }
                                }
                            })
                        }
                        Log.d(
                            "SurveyScreen",
                            "id: ${registerDto.id} | email: ${registerDto.email} | name: ${registerDto.name} | role: ${registerDto.role}"
                        )
                        Log.d(
                            "SurveyScreen",
                            "userId: ${surveyResponseDto.userId} | birth: ${surveyResponseDto.birth} | sex: ${surveyResponseDto.sex} | managerId: ${surveyResponseDto.managerId}"
                        )
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
            enabled = isSubmitPossible
        ) {
            Text(text = "제출", fontSize = 18.sp)
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun SurveyForm(
    sex: Int, // -1, 0, 1
    managerId: String, // 보호자 id
    onBirthChange: (String) -> Unit,
    onSexChange: (Int) -> Unit,
    onManagerIdChange: (String) -> Unit,
    onSubmitPossibleChange: (Boolean) -> Unit
) {
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var selectedMonth by remember { mutableStateOf<Int?>(null) }
    var selectedDay by remember { mutableStateOf<Int?>(null) }

    val genderOptions = listOf("성별을 선택하세요." to -1, "남자" to 0, "여자" to 1)
    val selectedGenderText = when (sex) {
        0 -> "남자"
        1 -> "여자"
        else -> "성별을 선택하세요."
    }

    var isBirthSelected by remember { mutableStateOf(false) }
    var isSexSelected by remember { mutableStateOf(false) }
    var isManagerIdSelected by remember { mutableStateOf(false) }

    // 조건이 변경될 때마다 버튼 활성화 여부를 재계산
    fun checkSubmitPossible() {
        val canSubmit = isBirthSelected && isSexSelected && isManagerIdSelected
        onSubmitPossibleChange(canSubmit)
    }

    Column(modifier = Modifier.fillMaxWidth(0.85f)) {
        BirthdaySelectorRow(selectedYear = selectedYear,
            selectedMonth = selectedMonth,
            selectedDay = selectedDay,
            onYearSelected = { newYear ->
                selectedYear = newYear
                // 년, 월, 일이 모두 선택되었으면 birth 업데이트
                if (selectedMonth != null && selectedDay != null) {
                    val birthString = String.format(
                        "%04d-%02d-%02d",  // 연도 4자리, 월/일 2자리
                        newYear, selectedMonth, selectedDay
                    )
                    onBirthChange(birthString)
                    isBirthSelected = true
                    checkSubmitPossible()

                    Log.d("SurveyForm", "Selected birth: $birthString")
                }
            },
            onMonthSelected = { newMonth ->
                selectedMonth = newMonth
                if (selectedYear != null && selectedDay != null) {
                    val birthString = String.format(
                        "%04d-%02d-%02d", selectedYear, newMonth, selectedDay
                    )
                    onBirthChange(birthString)
                    isBirthSelected = true
                    checkSubmitPossible()

                    Log.d("SurveyForm", "Selected birth: $birthString")
                }
            },
            onDaySelected = { newDay ->
                selectedDay = newDay
                if (selectedYear != null && selectedMonth != null) {
                    val birthString = String.format(
                        "%04d-%02d-%02d", selectedYear, selectedMonth, newDay
                    )
                    onBirthChange(birthString)
                    isBirthSelected = true
                    checkSubmitPossible()

                    Log.d("SurveyForm", "Selected birth: $birthString")
                }
            })

        GenderSelectionRow(label = "성별",
            selectedText = selectedGenderText,
            options = genderOptions,
            onValueSelected = { (_, value) ->
                onSexChange(value as Int)
                isSexSelected = (value != -1)
                checkSubmitPossible()

                Log.d("SurveyForm", "Selected sex: $value")
            })

        TextFieldRow(
            label = "보호자 아이디",
            value = managerId,
            placeholder = "보호자 아이디를 입력하세요.",
            onValueChange = {
                onManagerIdChange(it)
                // 입력 필드가 비어있지 않으면 true
                isManagerIdSelected = it.isNotEmpty()
                checkSubmitPossible()

                Log.d("SurveyForm", "Manager id: $it")
            })
    }
}
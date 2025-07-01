package com.project.feelrobot.ui.screens.findInfo

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.project.feelrobot.viewmodel.FindViewModel

@Composable
fun FindIdConfirmScreen(
    email: String, viewModel: FindViewModel = viewModel()
) {
    var foundId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(email) {
        viewModel.fetchIdByEmail(email,
            context,
            onResult = { foundId = it },
            onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        // 제목
        Text(
            text = "이메일 정보와 일치하는 아이디입니다.", fontSize = 14.sp, fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (foundId != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    // 1dp 회색 테두리
                    .border(
                        width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(8.dp)
                    )
                    // 흰 배경
                    .background(
                        color = Color.White, shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 16.dp)
            ) {
                // 가운데 정렬한 Row 안에 두 텍스트 배치
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "아이디 : ", fontSize = 16.sp, color = Color.Black
                    )
                    Text(
                        text = foundId!!,
                        fontSize = 16.sp,
                        color = Color(0xFF10298F),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            Text(
                text = "아이디를 불러오는 중입니다...", fontSize = 14.sp
            )
        }
    }
}

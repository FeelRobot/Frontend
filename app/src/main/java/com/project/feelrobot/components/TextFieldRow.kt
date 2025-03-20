package com.project.feelrobot.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextFieldRow(
    label: String,
    value: String,
    isPassword: Boolean = false,
    modifiable: Boolean = true,
    buttonText: String? = null, // 버튼 텍스트 (null이면 버튼 없음)
    onButtonClick: (() -> Unit)? = null // 버튼 클릭 이벤트
) {
    var text by remember { mutableStateOf("") }
    var isUserInput by remember { mutableStateOf(false) } // 사용자가 입력했는지 여부

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically // label과 입력 필드 정렬
    ) {
        // 라벨
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .width(100.dp)
                .padding(end = 8.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
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
                enabled = modifiable,
                visualTransformation = if (isPassword && isUserInput) PasswordVisualTransformation() else VisualTransformation.None,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.Black, // 입력하면 항상 검은색으로
                    fontSize = 14.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 버튼 (필요할 때만 추가)
        buttonText?.let {
            Button(
                onClick = { onButtonClick?.invoke() },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(40.dp)
                    .wrapContentWidth(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(text = it, fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

package com.project.feelrobot.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextFieldRow(
    label: String? = null, // id, password 등 label
    value: String, // 입력값
    placeholder: String, // 기본값 (플레이스홀더)
    onValueChange: (String) -> Unit, // 외부에서 상태 관리 가능하도록 추가
    isPassword: Boolean = false, // 비밀번호 입력 여부
    modifiable: Boolean = true, // 수정 가능 여부
    fontColor: Color = Color.Black, // 입력 폰트 색상
    buttonText: String? = null, // 버튼 텍스트 (null이면 버튼 없음)
    onButtonClick: (() -> Unit)? = null // 버튼 클릭 이벤트
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // label과 입력 필드 정렬
        ) {
            // label
            if (!label.isNullOrEmpty()) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .width(100.dp)
                        .padding(end = 8.dp)
                )
            }
            Row(
                modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 8.dp) // 내부 패딩 조정
                        .height(24.dp),
                    contentAlignment = Alignment.CenterStart // 플레이스홀더 및 입력 텍스트 정렬

                ) {
                    // 플레이스홀더 텍스트 (입력하기 전 회색 안내 문구)
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder, // 기본값을 안내 문구로 사용
                            fontSize = 14.sp, color = Color.Gray
                        )
                    }

                    // 실제 입력 필드
                    BasicTextField(
                        value = value,
                        onValueChange = { onValueChange(it) }, // 부모에서 관리하는 값 업데이트
                        enabled = modifiable,
                        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = fontColor, fontSize = 14.sp
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
                            .height(36.dp)
                            .wrapContentWidth(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = it, fontSize = 14.sp, modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // 밑줄 구분선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray)
        )
    }
}

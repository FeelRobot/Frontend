package com.project.feelrobot.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("DefaultLocale")
@Composable
fun BirthdaySelectorRow(
    selectedYear: Int?,
    selectedMonth: Int?,
    selectedDay: Int?,
    onYearSelected: (Int) -> Unit,
    onMonthSelected: (Int) -> Unit,
    onDaySelected: (Int) -> Unit,
    enabled: Boolean = true
) {
    // 드롭다운 확장 여부: 연, 월, 일 각각 별도의 상태
    var yearMenuExpanded by remember { mutableStateOf(false) }
    var monthMenuExpanded by remember { mutableStateOf(false) }
    var dayMenuExpanded by remember { mutableStateOf(false) }

    // 연도, 월, 일 리스트
    val years = (1950..2025).toList()
    val months = (1..12).toList()
    val days = (1..31).toList()

    // 월/일을 2자리 문자열("03", "09" 등)로 표시하기 위한 처리
    val monthText = if (selectedMonth != null) String.format("%02d", selectedMonth) else "00"
    val dayText = if (selectedDay != null) String.format("%02d", selectedDay) else "00"

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "생년월일",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier
                    .width(100.dp)
                    .padding(end = 8.dp)
            )

            // YYYY-MM-DD 형식
            // 3개의 Box 각각 클릭하면 해당 DropdownMenu가 열림
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {

                // ---- 연도 선택 영역 ----
                Box(modifier = Modifier
                    .clickable(enabled = enabled) { yearMenuExpanded = true }
                    .background(
                        color = Color(0xFFEFEFEF), shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (selectedYear != null) "$selectedYear" else "0000",
                            fontSize = 14.sp,
                            color = if (enabled) Color.Black else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "연도 선택",
                            tint = if (enabled) Color.Black else Color.Gray
                        )
                    }

                    // DropdownMenu (연도)
                    DropdownMenu(expanded = yearMenuExpanded,
                        onDismissRequest = { yearMenuExpanded = false }) {
                        years.forEach { y ->
                            DropdownMenuItem(text = { Text("${y}년") }, onClick = {
                                onYearSelected(y)
                                yearMenuExpanded = false
                                Log.d("BirthdaySelector", "Selected year: $y")

                            })
                        }
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "년", fontSize = 14.sp, color = Color.Black)

                Spacer(modifier = Modifier.width(8.dp))

                // ---- 월 선택 영역 ----
                Box(modifier = Modifier
                    .clickable(enabled = enabled) { monthMenuExpanded = true }
                    .background(
                        color = Color(0xFFEFEFEF), shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = monthText,
                            fontSize = 14.sp,
                            color = if (enabled) Color.Black else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "월 선택",
                            tint = if (enabled) Color.Black else Color.Gray
                        )
                    }

                    // DropdownMenu (월)
                    DropdownMenu(expanded = monthMenuExpanded,
                        onDismissRequest = { monthMenuExpanded = false }) {
                        months.forEach { m ->
                            DropdownMenuItem(text = { Text("${m}월") }, onClick = {
                                onMonthSelected(m)
                                monthMenuExpanded = false
                                Log.d("BirthdaySelector", "Selected month: $m")
                            })
                        }
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "월", fontSize = 14.sp, color = Color.Black)

                Spacer(modifier = Modifier.width(8.dp))

                // ---- 일 선택 영역 ----
                Box(modifier = Modifier
                    .clickable(enabled = enabled) { dayMenuExpanded = true }
                    .background(
                        color = Color(0xFFEFEFEF), shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dayText,
                            fontSize = 14.sp,
                            color = if (enabled) Color.Black else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "일 선택",
                            tint = if (enabled) Color.Black else Color.Gray
                        )
                    }

                    // DropdownMenu (일)
                    DropdownMenu(expanded = dayMenuExpanded,
                        onDismissRequest = { dayMenuExpanded = false }) {
                        days.forEach { d ->
                            DropdownMenuItem(text = { Text("${d}일") }, onClick = {
                                onDaySelected(d)
                                dayMenuExpanded = false
                                Log.d("BirthdaySelector", "Selected day: $d")
                            })
                        }
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "일", fontSize = 14.sp, color = Color.Black)
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

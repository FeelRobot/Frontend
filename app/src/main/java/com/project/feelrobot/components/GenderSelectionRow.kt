package com.project.feelrobot.components

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

@Composable
fun GenderSelectionRow(
    label: String, selectedText: String, options: List<Pair<String, Any>>, // ("남자", 0), ("여자", 1)
    onValueSelected: (Pair<String, Any>) -> Unit, enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier
                    .width(100.dp)
                    .padding(end = 8.dp)
            )

            Box(modifier = Modifier
                .clickable(enabled = enabled) {
                    expanded = true
                }
                .background(
                    color = Color(0xFFEFEFEF), shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 4.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedText.ifEmpty { "성별을 선택하세요" },
                        fontSize = 14.sp,
                        color = if (enabled) Color.Black else Color.Gray
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "성별 선택",
                        tint = if (enabled) Color.Black else Color.Gray,

                        )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(Color.White)
                ) {
                    options.forEach { (display, value) ->
                        DropdownMenuItem(text = { Text(text = display) }, onClick = {
                            onValueSelected(display to value)
                            expanded = false
                            Log.d("GenderSelection", "Selected gender: $display / $value")
                        }, enabled = enabled
                        )
                    }
                }
            }
        }

        // 밑줄
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray)
        )
    }
}
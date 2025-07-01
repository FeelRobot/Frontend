package com.project.feelrobot.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageLayout(
    pageTitle: String, content: @Composable () -> Unit
) {
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets(0, 0, 0, 0)),

        topBar = {
            Column {
                // 상단 AppBar
                TopAppBar(
                    title = {
                        Text(text = pageTitle)
                    },
                )
                // 바로 아래 구분선
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
            }
        }, content = {
            // paddingValues 적용하여 화면 콘텐츠 배치
            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
        })
}

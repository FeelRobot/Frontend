package com.project.feelrobot.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.feelrobot.storage.JwtTokenManager
import com.project.feelrobot.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel(), ) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var refreshToken by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val token = JwtTokenManager(context).refreshTokenFlow.first()
        refreshToken = token // 상태 업데이트
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "home", fontSize = 20.sp, color = Color.Black)
        Button(
            onClick = {
                coroutineScope.launch {
                    loginViewModel.logout(refreshToken!!, context, navController)
                }
            }, modifier = Modifier
                .fillMaxSize(0.4f)
                .height(50.dp)
        ) {
            Text(text = "로그아웃", fontSize = 18.sp)
        }
    }
}


package com.project.feelrobot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.feelrobot.ui.screens.SignupScreen

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule() // MainActivity 없이 독립적인 Compose 테스트

    @Test
    fun launchSignupScreen() {
        composeTestRule.setContent {
            SignupScreen() // SignupScreen 실행
        }

        // 계속 실행 상태 유지 (테스트 종료 방지)
        while (true) { }
    }
}
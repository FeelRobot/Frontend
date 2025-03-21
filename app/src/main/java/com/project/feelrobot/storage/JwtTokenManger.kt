package com.project.feelrobot.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class JwtTokenManager(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val AUTO_LOGIN_KEY = booleanPreferencesKey("auto_login")
    }

    // JWT 저장
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // JWT 불러오기
    val accessTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[ACCESS_TOKEN_KEY] }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[REFRESH_TOKEN_KEY] }

    // 자동 로그인 여부 저장
    suspend fun setAutoLogin(isAutoLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_LOGIN_KEY] = isAutoLogin
        }
    }

    // 자동 로그인 여부 가져오기
    val isAutoLoginFlow: Flow<Boolean> = context.dataStore.data.map { it[AUTO_LOGIN_KEY] ?: false }

    // JWT 삭제 (로그아웃 시)
    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(AUTO_LOGIN_KEY)
        }
    }
}

package com.d108.moyeo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserDataManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

        private val PIN_KEY = stringPreferencesKey("pin")
        private val BIOMETRICS_PREFERENCE_KEY = booleanPreferencesKey("biometrics_preference")
    }

    // --- Access Token 관련 ---
    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN_KEY] }
    suspend fun getAccessToken(): String? = accessTokenFlow.first()

    // --- Refresh Token 관련 ---
    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }

    // --- 공용 함수 ---
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }

    /*
    핀 관련
     */
    suspend fun savePin(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[PIN_KEY] = pin
        }
    }
    // PIN을 FLOW로 제공
    val pinFlow: Flow<String?> = context.dataStore.data.map { it[PIN_KEY] }

    /*
    생체 인증 관련
     */
    suspend fun saveBiometricsPreference(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRICS_PREFERENCE_KEY] = isEnabled
        }
    }

    val biometricsPreferenceFlow: Flow<Boolean> = context.dataStore.data.map {
        it[BIOMETRICS_PREFERENCE_KEY] ?: false  // 기본값 false
    }
}
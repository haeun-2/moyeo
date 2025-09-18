package com.d108.moyeo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
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
        private val WALLET_COLOR_KEY = intPreferencesKey("wallet_color")
        private val WALLET_NAME_KEY = stringPreferencesKey("wallet_name")
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

    /*
    개인 박스 관련
     */
    val walletColorFlow: Flow<Int?> = context.dataStore.data.map { it[WALLET_COLOR_KEY] }
    suspend fun saveWalletColor(color: Int) {
        context.dataStore.edit { prefs -> prefs[WALLET_COLOR_KEY] = color }
    }

    val walletNameFlow: Flow<String?> =
        context.dataStore.data.map { it[WALLET_NAME_KEY] }
    suspend fun saveWalletName(name: String) {
        context.dataStore.edit { it[WALLET_NAME_KEY] = name }
    }

    /*
    그룹 박스 관련
     */
    private fun groupNameKey(id: Long)  = stringPreferencesKey("group_name_$id")
    private fun groupColorKey(id: Long) = intPreferencesKey("group_color_$id")

    suspend fun saveGroupName(id: Long, name: String) {
        context.dataStore.edit { it[groupNameKey(id)] = name }
    }
    suspend fun saveGroupColor(id: Long, argb: Int) {
        context.dataStore.edit { it[groupColorKey(id)] = argb }
    }
    suspend fun getGroupName(id: Long): String? =
        context.dataStore.data.map { it[groupNameKey(id)] }.firstOrNull()
    suspend fun getGroupColor(id: Long): Int? =
        context.dataStore.data.map { it[groupColorKey(id)] }.firstOrNull()

}
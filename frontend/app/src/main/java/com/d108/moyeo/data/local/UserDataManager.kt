package com.d108.moyeo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Jetpack DataStore를 사용하여 사용자의 로컬 설정 및 인증 정보를 영구적으로 관리
 * 앱의 로컬 데이터베이스 역할
 *
 * 관리하는 데이터 종류:
 * - 인증 정보: Access/Refresh 토큰
 * - 앱 설정: PIN, 생체 인증 사용 여부
 * - 사용자 커스텀 데이터: 개인 지갑 색상, 그룹 박스별 커스텀 색상, 그룹 박스 즐겨찾기 목록
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserDataManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val PIN_KEY = stringPreferencesKey("pin")
        private val BIOMETRICS_PREFERENCE_KEY = booleanPreferencesKey("biometrics_preference")
        // --- 개인 박스(지갑) 관련
        private val WALLET_COLOR_KEY = intPreferencesKey("wallet_color")

        // --- 그룹 박스 관련 Key ---
        private val BOOKMARKED_GROUP_IDS = stringSetPreferencesKey("bookmarked_group_ids")
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
    * 이름 관련
    */
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { prefs -> prefs[USER_NAME_KEY] = name }
    }
    // 이름을 FLOW로 제공
    val userNameFlow: Flow<String?> = context.dataStore.data.map { it[USER_NAME_KEY] }

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
    그룹 박스 관련
     */

    // 색깔
    private fun groupColorKey(id: Long) = intPreferencesKey("group_color_$id")


    suspend fun saveGroupColor(id: Long, argb: Int) {
        context.dataStore.edit { it[groupColorKey(id)] = argb }
    }

    suspend fun getGroupColor(id: Long): Int? =
        context.dataStore.data.map { it[groupColorKey(id)] }.firstOrNull()


    /*
    개인 박스 관련
     */
    val walletColorFlow: Flow<Int?> = context.dataStore.data.map { it[WALLET_COLOR_KEY] }

    suspend fun saveWalletColor(color: Int) {
        context.dataStore.edit { prefs -> prefs[WALLET_COLOR_KEY] = color }
    }

    // --- 즐겨찾기 관련

    // 즐겨찾기된 모든 그룹 박스의 ID 목록을 실시간으로 관찰(observe)할 수 있는 Flow
    val bookmarkedGroupIdsFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[BOOKMARKED_GROUP_IDS] ?: emptySet()
        }

    suspend fun isBookmarked(id: Long): Boolean {
        val currentIds = bookmarkedGroupIdsFlow.first()
        return currentIds.contains(id.toString())
    }

    suspend fun addBookmark(id: Long) {
        context.dataStore.edit { preferences ->
            val currentIds = preferences[BOOKMARKED_GROUP_IDS] ?: emptySet()
            preferences[BOOKMARKED_GROUP_IDS] = currentIds + id.toString()
        }
    }

    suspend fun deleteBookmark(id: Long) {
        context.dataStore.edit { preferences ->
            val currentIds = preferences[BOOKMARKED_GROUP_IDS] ?: emptySet()
            preferences[BOOKMARKED_GROUP_IDS] = currentIds - id.toString()
        }
    }

//    private fun groupNameKey(id: Long) = stringPreferencesKey("group_name_$id")
//    suspend fun saveGroupName(id: Long, name: String) {
//        context.dataStore.edit { it[groupNameKey(id)] = name }
//    }
//    suspend fun getGroupName(id: Long): String? =
//        context.dataStore.data.map { it[groupNameKey(id)] }.firstOrNull()

}
package com.d108.moyeo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Context.dataStore 확장 함수를 사용하여 앱 전체에서 'auth_prefs'라는 이름의 DataStore 인스턴스를 공유
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

/**
 * AccessToken과 RefreshToken을 기기에 안전하게 저장하고 불러오는 클래스
 * Hilt가 이 클래스를 싱글턴으로 관리하여, 앱의 어느 곳에서든 동일한 인스턴스를 주입받아 사용
 */
@Singleton
class AuthDataStore @Inject constructor(
    // @ApplicationContext 어노테이션은 Hilt에게 애플리케이션의 Context를 주입해달라고 요청하는 것
    @ApplicationContext private val context: Context
) {
    //  DataStore에 데이터를 저장할 때 사용할 키(Key)를 미리 정의합니다.
    // 'stringPreferencesKey'는 이 키가 문자열(String) 값을 저장할 것임을 나타냅니다.
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    // AccessToken을 Flow 형태로 외부에 제공합니다.
    // Flow를 사용하면, 토큰 값이 변경될 때마다 이 값을 구독(collect)하는 곳에서 자동으로 새로운 값을 받을 수 있습니다.
    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    // RefreshToken을 Flow 형태로 외부에 제공합니다.
    val refreshToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    // 새로운 토큰들을 DataStore에 저장하는 함수입니다.
    // 'suspend' 키워드는 이 함수가 비동기적으로 실행되어야 함을 나타냅니다. (UI 스레드를 막지 않음)
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // 모든 토큰을 삭제하는 함수입니다. (로그아웃 시 사용)
    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
package com.d108.moyeo.presentation.ui.screen.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.data.local.UserDataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val userDataManager: UserDataManager
) : ViewModel() {

    // DataStore의 생체인증 여부를 그대로 구독
    val biometricEnabled = userDataManager.biometricsPreferenceFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // 스위치 토글 시 DataStore에 즉시 저장
    fun onBiometricToggle(enabled: Boolean) {
        viewModelScope.launch {
            userDataManager.saveBiometricsPreference(enabled)
        }
    }
}

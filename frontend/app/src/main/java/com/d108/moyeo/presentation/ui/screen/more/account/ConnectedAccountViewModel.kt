package com.d108.moyeo.presentation.ui.screen.more.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.usecase.account.GetConnectedAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectedAccountViewModel @Inject constructor(
    private val getConnectedAccountUseCase: GetConnectedAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectedAccountUiState())
    val uiState: StateFlow<ConnectedAccountUiState> = _uiState

    init {
        loadAccount()
    }

    fun loadAccount() {
        viewModelScope.launch {
            _uiState.value = ConnectedAccountUiState(isLoading = true)
            val result = getConnectedAccountUseCase()
            _uiState.value = result.fold(
                onSuccess = { ConnectedAccountUiState(account = it) },
                onFailure = { ConnectedAccountUiState(error = it.message) }
            )
        }
    }
}
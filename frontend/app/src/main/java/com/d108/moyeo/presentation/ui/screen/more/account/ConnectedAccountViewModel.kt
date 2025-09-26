package com.d108.moyeo.presentation.ui.screen.more.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.usecase.account.GetConnectedAccountUseCase
import com.d108.moyeo.domain.usecase.signup.GetAllBankListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectedAccountViewModel @Inject constructor(
    private val getConnectedAccountUseCase: GetConnectedAccountUseCase,
    private val getAllBankListUseCase: GetAllBankListUseCase
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
            val bankListResult = getAllBankListUseCase()

            val nextState = result.fold(
                onSuccess = { account ->
                    val banks = bankListResult.getOrNull().orEmpty()
                    val matchedBank = banks.firstOrNull { it.name == account.bankName }
                    ConnectedAccountUiState(
                        isLoading = false,
                        account = account,
                        selectedBank = matchedBank,
                        error = null
                    )
                },
                onFailure = { error ->
                    ConnectedAccountUiState(
                        isLoading = false,
                        account = null,
                        selectedBank = null,
                        error = error.message
                    )
                }
            )

            _uiState.value = nextState
        }
    }
}
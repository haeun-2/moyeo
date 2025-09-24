package com.d108.moyeo.presentation.ui.screen.more.account.change

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.model.Bank
import com.d108.moyeo.domain.usecase.account.ConnectAccountUseCase
import com.d108.moyeo.domain.usecase.account.RequestAccountVerificationUseCase
import com.d108.moyeo.domain.usecase.signup.GetAllBankListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountChangeViewModel @Inject constructor(
    private val requestVerification: RequestAccountVerificationUseCase,
    private val connectAccount: ConnectAccountUseCase,
    private val getAllBankList: GetAllBankListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountChangeUiState())
    val uiState = _uiState.asStateFlow()

    private val _banks = MutableStateFlow<List<Bank>>(emptyList())
    val banks = _banks.asStateFlow()
    private val _isBanksLoading = MutableStateFlow(false)
    val isBanksLoading = _isBanksLoading.asStateFlow()
    private val _banksError = MutableStateFlow<String?>(null)
    val banksError = _banksError.asStateFlow()

    fun ensureBanksLoadedForSheet() {
        if (_banks.value.isEmpty() && !_isBanksLoading.value) {
            loadBanks()
        }
    }

    fun onBankSelected(bank: Bank) {
        _uiState.value = _uiState.value.copy(
            bankCode = bank.code,
            bankName = bank.name,
            error = null
        )
    }
    fun updateBankAccount(acc: String) {
        _uiState.value = _uiState.value.copy(bankAccount = acc, error = null)
    }
    fun updateVerificationCode(code: String) {
        _uiState.value = _uiState.value.copy(verificationCode = code, error = null)
    }

    fun submitAccountInput(onNext: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            val result = requestVerification(state.bankCode, state.bankAccount)
            _uiState.value = result.fold(
                onSuccess = {
                    _uiState.value.copy(isLoading = false, step = AccountChangeStep.VERIFY)
                },
                onFailure = { ex ->
                    _uiState.value.copy(isLoading = false, error = ex.message ?: "인증 요청 실패")
                }
            )
            if (_uiState.value.error == null) onNext()
        }
    }

    fun submitVerification(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            val result = connectAccount(state.bankCode, state.bankAccount, state.verificationCode)
            _uiState.value = result.fold(
                onSuccess = {
                    _uiState.value.copy(isLoading = false, step = AccountChangeStep.DONE)
                },
                onFailure = { ex ->
                    _uiState.value.copy(isLoading = false, error = ex.message ?: "계좌 변경 실패")
                }
            )
            if (_uiState.value.step == AccountChangeStep.DONE) onSuccess()
        }
    }

    private fun loadBanks() {
        viewModelScope.launch {
            _isBanksLoading.value = true
            _banksError.value = null
            val result = getAllBankList()
            result
                .onSuccess { _banks.value = it }
                .onFailure { _banksError.value = it.message ?: "은행 목록을 불러오지 못했습니다." }
            _isBanksLoading.value = false
        }
    }

    fun backToInputStep() {
        _uiState.value = _uiState.value.copy(
            step = AccountChangeStep.INPUT,
            error = null,
            isLoading = false
        )
    }
}
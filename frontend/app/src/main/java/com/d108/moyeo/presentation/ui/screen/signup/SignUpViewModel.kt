package com.d108.moyeo.presentation.ui.screen.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.domain.model.Bank
import com.d108.moyeo.domain.model.SignUpInfo
import com.d108.moyeo.domain.usecase.auth.LoginUseCase
import com.d108.moyeo.domain.usecase.signup.GetAllBankListUseCase
import com.d108.moyeo.domain.usecase.signup.RequestAccountAuthUseCase
import com.d108.moyeo.domain.usecase.signup.RequestEmailAuthUseCase
import com.d108.moyeo.domain.usecase.signup.RequestPhoneAuthUseCase
import com.d108.moyeo.domain.usecase.signup.SubmitSignUpUseCase
import com.d108.moyeo.domain.usecase.signup.VerifyAccountCodeUseCase
import com.d108.moyeo.domain.usecase.signup.VerifyEmailCodeUseCase
import com.d108.moyeo.domain.usecase.signup.VerifyPhoneCodeUseCase
import com.d108.moyeo.domain.usecase.user.GetFidUseCase
import com.d108.moyeo.domain.usecase.user.SaveBiometricsPreferenceUseCase
import com.d108.moyeo.domain.usecase.user.SavePinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val TAG = "signupviewmodel"
// 회원가입 과정의 모든 상태를 담는 데이터 클래스


// UI로 전달할 일회성 탐색 이벤트
sealed class SignUpNavigationEvent {
    object NavigateToHome : SignUpNavigationEvent()
    object NavigateBack : SignUpNavigationEvent() // 뒤로가기 이벤트 추가
    object ShowBiometricPrompt : SignUpNavigationEvent()  // 생체 인증 프롬프트창
}

@HiltViewModel
class SignUpViewModel @Inject constructor (
    private val requestEmailAuthUseCase: RequestEmailAuthUseCase, // Repository 대신 UseCase를 주입받음
    private val verifyEmailCodeUseCase: VerifyEmailCodeUseCase,
    private val requestPhoneAuthUseCase: RequestPhoneAuthUseCase,
    private val verifyPhoneCodeUseCase: VerifyPhoneCodeUseCase,
    private val getAllBankListUseCase: GetAllBankListUseCase,
    private val requestAccountAuthUseCase: RequestAccountAuthUseCase,
    private val verifyAccountCodeUseCase: VerifyAccountCodeUseCase,
    private val submitSignUpUseCase: SubmitSignUpUseCase,
    private val savePinUseCase: SavePinUseCase,
    private val saveBiometricsPreferenceUseCase: SaveBiometricsPreferenceUseCase,
    private val getFidUseCase: GetFidUseCase,
    private val loginUseCase: LoginUseCase,
    private val userDataManager: UserDataManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    // 화면 이동과 같은 일회성 이벤트를 전달하기 위한 SharedFlow
    private val _navigationEvent = MutableSharedFlow<SignUpNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        // ViewModel이 생성될 때 은행 목록을 미리 불러옵니다.
        loadBankList()
    }

    private fun loadBankList() {
        viewModelScope.launch {
            getAllBankListUseCase()
                .onSuccess { banks ->
                    _uiState.update { it.copy(bankList = banks) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = "은행 목록을 불러오지 못했습니다.") }
                }
        }
    }

    fun onNextClicked() {
        when (val currentStep = _uiState.value.currentStep) {
            // '실제 일'을 하도록 수정
            SignUpStep.EMAIL_INPUT -> requestEmailAuth()
            SignUpStep.EMAIL_VERIFY -> verifyEmailCode()
            SignUpStep.PHONE_INPUT -> requestPhoneAuth()
            SignUpStep.PHONE_VERIFY -> verifyPhoneCode()
            SignUpStep.ACCOUNT -> requestAccountAuth()
            SignUpStep.ACCOUNT_VERIFY -> verifyAccountCode()
            SignUpStep.COMPLETE -> submitFinalSignUp()

            // BIOMETRICS와 COMPLETE는 특별 처리
            SignUpStep.BIOMETRICS -> viewModelScope.launch { _navigationEvent.emit(SignUpNavigationEvent.ShowBiometricPrompt) }

            // 그 외 단순 화면 전환만 필요한 경우
            else -> {
                val nextStep = when (currentStep) {
                    SignUpStep.NAME -> SignUpStep.EMAIL_INPUT
                    SignUpStep.TERMS -> SignUpStep.PIN
                    SignUpStep.PIN -> SignUpStep.PIN_CONFIRM
                    SignUpStep.PIN_CONFIRM -> SignUpStep.BIOMETRICS
                    else -> null
                }
                if (nextStep != null) {
                    _uiState.update { it.copy(currentStep = nextStep) }
                }
            }
        }
    }

    fun onBackClicked() {
        val currentStep = _uiState.value.currentStep
        val previousStep = when (currentStep) {
            SignUpStep.EMAIL_INPUT -> SignUpStep.NAME
            SignUpStep.EMAIL_VERIFY -> SignUpStep.EMAIL_INPUT
            SignUpStep.PHONE_INPUT -> SignUpStep.EMAIL_VERIFY
            SignUpStep.PHONE_VERIFY -> SignUpStep.PHONE_INPUT
            SignUpStep.ACCOUNT -> SignUpStep.PHONE_VERIFY
            SignUpStep.ACCOUNT_VERIFY -> SignUpStep.ACCOUNT
            SignUpStep.TERMS -> SignUpStep.ACCOUNT_VERIFY
            SignUpStep.PIN -> SignUpStep.TERMS
            SignUpStep.PIN_CONFIRM -> SignUpStep.PIN
            SignUpStep.BIOMETRICS -> SignUpStep.PIN_CONFIRM
            SignUpStep.COMPLETE -> SignUpStep.BIOMETRICS
            else -> null // 첫 단계(NAME)에서는 이전 단계가 없음
        }

        if (previousStep != null) {
            // 이전 단계가 있으면 상태만 업데이트
            _uiState.update { it.copy(currentStep = previousStep) }
        } else {
            // 첫 단계에서 뒤로가기를 누르면, 화면을 닫으라는 이벤트를 발생시킴
            viewModelScope.launch {
                _navigationEvent.emit(SignUpNavigationEvent.NavigateBack)
            }
        }
    }

    // 각 데이터 변경 시 호출될 함수들
    // 이름 단계
    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    // 이메일 단계
    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onEmailCodeChanged(emailCode: String) {
        _uiState.update { it.copy(emailCode = emailCode) }
    }

    /**
     * 이메일 인증 코드 전송을 서버에 요청합니다.
     */
    fun requestEmailAuth() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            requestEmailAuthUseCase(uiState.value.email)
                .onSuccess { sessionId ->
                    _uiState.update { it.copy(isLoading = false, sessionId = sessionId, currentStep = SignUpStep.EMAIL_VERIFY) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "실패!") }
                }
        }
    }

    /**
     * 사용자가 입력한 인증 코드를 서버에 검증 요청합니다.
     */
    fun verifyEmailCode() {
        val currentState = uiState.value
        if (currentState.sessionId == null) {
            _uiState.update { it.copy(errorMessage = "세션 정보가 없습니다. 다시 시도해주세요.") }
            return
        }

        Log.d(TAG, "이메일 코드 검증 직전 sessionId: ${currentState.sessionId}")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Repository 대신 주입받은 UseCase를 직접 호출합니다
            verifyEmailCodeUseCase(
                sessionId = currentState.sessionId,
                email = currentState.email,
                emailCode = currentState.emailCode
            )
                .onSuccess {
                    Log.d(TAG, "이메일 코드 검증 성공")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isEmailVerified = true,
                            currentStep = SignUpStep.PHONE_INPUT
                        )
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "이메일 코드 검증 실패", error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "인증번호가 올바르지 않습니다.") }
                }
        }
    }

    // 휴대폰 단계
    fun onPhoneNumberChanged(phoneNumber: String) {
        _uiState.update { it.copy(phoneNumber = phoneNumber) }
    }

    fun onPhoneCodeChanged(code: String) {
        _uiState.update { it.copy(phoneCode = code) }
    }

    fun requestPhoneAuth() {
        val currentState = uiState.value
        if (currentState.sessionId == null) {
            _uiState.update { it.copy(errorMessage = "세션 정보가 없습니다.") }
            return
        }

        Log.d(TAG, "전화번호 코드 검증 직전 sessionId: ${currentState.sessionId}")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            requestPhoneAuthUseCase(currentState.sessionId, currentState.phoneNumber)
                .onSuccess { response ->
                    Log.d(TAG, "전화번호 인증 요청 성공: new session = ${response.sessionId}")
                    _uiState.update { it.copy(isLoading = false, sessionId = response.sessionId, currentStep = SignUpStep.PHONE_VERIFY) }
                }
                .onFailure { error ->
                    Log.e(TAG, "전화번호 인증 요청 실패", error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "인증 요청에 실패했습니다.") }
                }
        }
    }

    fun verifyPhoneCode() {
        val currentState = uiState.value
        if (currentState.sessionId == null) {
            _uiState.update { it.copy(errorMessage = "세션 정보가 없습니다.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            verifyPhoneCodeUseCase(
                sessionId = currentState.sessionId,
                phoneNumber = currentState.phoneNumber,
                phoneCode = currentState.phoneCode
            )
                .onSuccess { response ->
                    Log.d(TAG, "전화번호 코드 검증 성공: new session = ${response.sessionId}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPhoneNumberVerified = true,
                            sessionId = response.sessionId,
                            currentStep = SignUpStep.ACCOUNT
                        )
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "전화번호 코드 검증 실패", error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "인증번호가 올바르지 않습니다.") }
                }
        }
    }

    /**
     * [디버그용] 실제 SMS 인증을 건너뛰고 전화번호를 즉시 '인증됨' 상태로 처리합니다.
     */
    fun onDebugPhoneNumberVerified() {
        // 이 함수는 실제 출시 버전에는 포함되지 않도록 UI 단에서 제어해야 합니다.
        _uiState.update {
            it.copy(
                isPhoneNumberVerified = true,
                currentStep = SignUpStep.ACCOUNT // 인증 성공 시 다음 단계인 계좌 입력으로 이동
            )
        }
    }

    fun onBankFieldClicked() {
        _uiState.update { it.copy(showBankBottomSheet = true) }
    }

    fun onBankSelected(bank: Bank) {
        _uiState.update { it.copy(accountBank = bank, showBankBottomSheet = false) }
    }

    fun onBankBottomSheetDismiss() {
        _uiState.update { it.copy(showBankBottomSheet = false) }
    }

    fun onAccountNumberChanged(accountNumber: String) {
        _uiState.update { it.copy(accountNumber = accountNumber) }
    }

    // 1원 인증단계
    fun onOneCoinNumberChanged(oneCoinNumber: String) {
        _uiState.update { it.copy(oneCoinNumber = oneCoinNumber) }
    }

    fun requestAccountAuth() {
        val currentState = uiState.value
        if (currentState.sessionId == null || currentState.accountBank == null) {
            _uiState.update { it.copy(errorMessage = "필수 정보가 누락되었습니다.") }
            return
        }

        Log.d(TAG, "1원인증 요청 직전 sessionId: ${currentState.sessionId}")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            requestAccountAuthUseCase(
                sessionId = currentState.sessionId,
                email = currentState.email,
                bankAccount = currentState.accountNumber
            )
                .onSuccess { response ->
                    Log.d(TAG, "계좌 인증 요청 성공: new session = ${response.sessionId}")
                    _uiState.update { it.copy(isLoading = false, sessionId = response.sessionId, currentStep = SignUpStep.ACCOUNT_VERIFY) }
                }
                .onFailure { error ->
                    Log.e(TAG, "계좌 인증 요청 실패", error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "계좌 인증에 실패했습니다.") }
                }
        }
    }

    /**
     * 사용자가 입력한 1원 인증 코드를 서버에 검증 요청합니다.
     */
    fun verifyAccountCode() {
        val currentState = uiState.value
        if (currentState.sessionId == null) {
            _uiState.update { it.copy(errorMessage = "세션 정보가 없습니다.") }
            return
        }

        Log.d(TAG, "1원인증 검증 직전 sessionId: ${currentState.sessionId}")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            verifyAccountCodeUseCase(
                sessionId = currentState.sessionId,
                email = currentState.email,
                bankAccount = currentState.accountNumber,
                code = currentState.oneCoinNumber
            )
                .onSuccess { response ->
                    Log.d(TAG, "계좌 코드 검증 성공: new session = ${response.sessionId}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isOneCoinVerified = true,
                            sessionId = response.sessionId,
                            currentStep = SignUpStep.TERMS
                        )
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "계좌 코드 검증 실패", error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "인증번호가 올바르지 않습니다.") }
                }
        }
    }

    // 약관 동의 상태
    fun onAllTermsChanged(isChecked: Boolean) {
        _uiState.update {
            it.copy(
                allTermsAccepted = isChecked,
                termsOfServiceAccepted = isChecked,
                privacyPolicyAccepted = isChecked
            )
        }
    }

    fun onTermsOfServiceChanged(isChecked: Boolean) {
        _uiState.update {
            val allChecked = isChecked && it.privacyPolicyAccepted
            it.copy(termsOfServiceAccepted = isChecked, allTermsAccepted = allChecked)
        }
    }

    fun onPrivacyPolicyChanged(isChecked: Boolean) {
        _uiState.update {
            val allChecked = isChecked && it.termsOfServiceAccepted
            it.copy(privacyPolicyAccepted = isChecked, allTermsAccepted = allChecked)
        }
    }


    // 6자리 핀번호 입력
    fun onPinInput(digit: String, isConfirm: Boolean) {
        if (isConfirm) {
            if (_uiState.value.pinConfirm.length < 6) {
                _uiState.update { it.copy(pinConfirm = it.pinConfirm + digit) }
            }
        } else {
            if (_uiState.value.pin.length < 6) {
                _uiState.update { it.copy(pin = it.pin + digit) }
            }
        }
    }

    fun onPinBackspace(isConfirm: Boolean) {
        if (isConfirm) {
            _uiState.update { it.copy(pinConfirm = it.pinConfirm.dropLast(1)) }
        } else {
            _uiState.update { it.copy(pin = it.pin.dropLast(1)) }
        }
    }

    fun onPinClear(isConfirm: Boolean) {
        if (isConfirm) {
            _uiState.update { it.copy(pinConfirm = "") }
        } else {
            _uiState.update { it.copy(pin = "") }
        }
    }


    // 생체 인증
    // "건너뛰기" 버튼 클릭 시 호출
    fun skipBiometrics() {
        _uiState.update { it.copy(
            isBiometricsUsed = false,
            currentStep = SignUpStep.COMPLETE
        )}
    }

    // 실제 지문 인증이 성공했을 때 호출될 함수
    fun onBiometricsSucceeded() {
        _uiState.update { it.copy(
            isBiometricsUsed = true,
            currentStep = SignUpStep.COMPLETE // 완료 단계로 이동
        )}
    }

    private fun submitFinalSignUp() {
        val currentState = uiState.value
        if (currentState.sessionId == null || currentState.accountBank == null) {
            _uiState.update { it.copy(errorMessage = "필수 정보가 누락되었습니다.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 최종 제출 직전에 FID 받음
            getFidUseCase()
                .onSuccess { fid ->
                    Log.d("SignUpViewModel", "FID 가져오기 성공: $fid")

                    val signUpInfo = SignUpInfo(
                        name = currentState.name,
                        email = currentState.email,
                        phoneNumber = currentState.phoneNumber,
                        bankCode = currentState.accountBank.code,
                        accountNumber = currentState.accountNumber,
                        fid = fid // 실제 FID 값으로 교체
                    )

                    submitSignUpUseCase(currentState.sessionId, signUpInfo)
                        .onSuccess {
                            Log.d("SignUpViewModel", "최종 회원가입 성공!")
                            autoLoginAfterSignUp(signUpInfo)
                        }
                        .onFailure { error ->
                            Log.e("SignUpViewModel", "최종 회원가입 실패", error)
                            _uiState.update { it.copy(isLoading = false, errorMessage = "회원가입에 실패했습니다.") }
                        }
                }
                .onFailure { error ->
                    Log.e("SignUpViewModel", "FID 가져오기 실패", error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "기기 ID를 가져오지 못했습니다.") }
                }
        }
    }

    private suspend fun autoLoginAfterSignUp(signUpInfo: SignUpInfo) {
        Log.d("SignUpViewModel", "회원가입 성공 후 자동 로그인 시도...")
        loginUseCase(signUpInfo.phoneNumber, signUpInfo.fid)
            .onSuccess { token ->
                Log.d("SignUpViewModel", "자동 로그인 성공! AccessToken: ${token.accessToken}")
                savePinUseCase(uiState.value.pin)
                saveBiometricsPreferenceUseCase(uiState.value.isBiometricsUsed)
                viewModelScope.launch {
                    userDataManager.saveUserName(signUpInfo.name)
                }
                _navigationEvent.emit(SignUpNavigationEvent.NavigateToHome)
            }
            .onFailure { error ->
                Log.e("SignUpViewModel", "자동 로그인 실패", error)
                // 자동 로그인은 실패했지만, 회원가입 자체는 성공했으므로
                // 사용자가 직접 로그인할 수 있도록 로그인 화면(또는 FirstScreen)으로 보냅니다.
                _uiState.update { it.copy(isLoading = false, errorMessage = "회원가입은 완료되었으나, 자동 로그인에 실패했습니다.") }
                _navigationEvent.emit(SignUpNavigationEvent.NavigateBack)
            }
    }

}

package com.d108.moyeo.presentation.ui.screen.currency.reservation

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.util.BiometricAuthManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    navController: NavController,
    viewModel: ReservationViewModel = hiltViewModel(),
    initialBoxId: Long? = null,
    entry: String = "home"
) {
    val uiState by viewModel.uiState.collectAsState()
    val boxes by viewModel.boxes.collectAsState(initial = emptyList())

    val context = LocalContext.current
    val activity = context as FragmentActivity
    val biometricManager = remember { BiometricAuthManager(activity) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // 오늘 날짜 자정 (UTC 밀리초)
    val todayMillis = remember {
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.periodStart.toMillis().takeIf { it > 0 } ?: todayMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= todayMillis
            }
        )

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val selectedDateStr = it.toFormattedString()
                            viewModel.changePeriodStart(selectedDateStr)
                            // 시작일이 종료일보다 늦으면 종료일 초기화
                            if (uiState.periodEnd.isNotEmpty() && selectedDateStr.toMillis() >= uiState.periodEnd.toMillis()) {
                                viewModel.changePeriodEnd("")
                            }
                        }
                        showStartDatePicker = false
                    }
                ) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("취소") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // --- 종료일 DatePickerDialog ---
    if (showEndDatePicker) {
        val startDateMillis = remember(uiState.periodStart) { uiState.periodStart.toMillis() }
        val sixMonthsLaterMillis = remember(startDateMillis) {
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = startDateMillis
                add(Calendar.MONTH, 6)
            }.timeInMillis
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.periodEnd.toMillis().takeIf { it > 0 },
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis > startDateMillis && utcTimeMillis <= sixMonthsLaterMillis
            }
        )

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.changePeriodEnd(it.toFormattedString()) }
                        showEndDatePicker = false
                    }
                ) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("취소") }
            }
        ) { DatePicker(state = datePickerState) }
    }


    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is ReservationViewModel.ReservationNavEvent.ShowBiometricPrompt -> {
                    if (biometricManager.canAuthenticate()) {
                        biometricManager.authenticate(
                            title = "본인 인증",
                            negativeButtonText = "PIN으로 인증하기",
                            onSuccess = { viewModel.onBiometricsSucceeded() },
                            onError = { code, _ ->
                                when (code) {
                                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                                    BiometricPrompt.ERROR_LOCKOUT,
                                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> viewModel.skipBiometrics()
                                    else -> { /* 필요 시 토스트 */ }
                                }
                            },
                            onFailed = { /* 필요 시 토스트 */ }
                        )
                    } else {
                        viewModel.skipBiometrics()
                    }
                }
            }
        }
    }

    LaunchedEffect(initialBoxId, entry) {
        if (entry.equals("currency", ignoreCase = true)) {
            initialBoxId?.takeIf { it > 0 }?.let(viewModel::startFromCurrency)
        }
    }

    LaunchedEffect(uiState.step) {
        if (uiState.step == ReservationStep.Home) {
            viewModel.refreshReservations()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("예약 환전") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.goPrev()
                        if (uiState.step == ReservationStep.Home) navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                when (uiState.step) {

                    ReservationStep.BoxSelection -> {
                        TargetBoxContent(
                            selectedBoxId = uiState.selectedBoxId,
                            onBoxSelect = viewModel::selectBox,
                            boxes = boxes,
                        )
                    }

                    ReservationStep.Home -> {
                        HomeContent(
                            paddingValues = paddingValues,
                            isLoading = uiState.isLoading,
                            reservations = uiState.reservations.map {
                                ReservationListItem(
                                    id = it.id.toString(),
                                    currencyCode = it.toCurrency,
                                    currencyName = it.toCurrency, // 필요시 CurrencyUnit 맵으로 이름 변환
                                    targetRate = String.format("%,.2f", it.targetRate),
                                    amountKrw = "%,d".format(it.amount),
                                    expiresAt = it.expiresAt
                                )
                            },
                            onRefresh = viewModel::refreshReservations,
                            onStartReservation = viewModel::goCurrencySelectionIfBoxSelected
                        )
                    }

                    ReservationStep.CurrencySelection -> {
                        ChooseCurrencyContent(
                            paddingValues = paddingValues,
                            selected = uiState.toCurrencyCode,
                            onSelect = { code, _ -> viewModel.selectCurrency(code) },
                        )
                    }

                    ReservationStep.RateInput -> {
                        RateInputContent(
                            paddingValues = paddingValues,
                            currencyCode = uiState.toCurrencyCode,
                            input = uiState.targetRate,
                            onInputChange = viewModel::changeTargetRate,
                        )
                    }

                    ReservationStep.AmountInput -> {
                        AmountInputContent(
                            paddingValues = paddingValues,
                            currencyCode = uiState.toCurrencyCode.ifBlank { "—" },
                            selectedTab = uiState.selectedTab,          // "KRW" or toCurrencyCode
                            inputAmount = uiState.inputAmount,
                            krwValue = uiState.krwValue,
                            foreignValue = uiState.foreignValue,
                            onTabChange = viewModel::changeAmountTab,
                            onInputChange = viewModel::changeAmountInput,
                        )
                    }

                    ReservationStep.PeriodSelection -> {
                        PeriodSelectionContent(
                            paddingValues = paddingValues,
                            startDate = uiState.periodStart,
                            endDate = uiState.periodEnd,
                            onStartDateClick = { showStartDatePicker = true },
                            onEndDateClick = {
                                if (uiState.periodStart.isNotEmpty()) {
                                    showEndDatePicker = true
                                } else {
                                    Toast.makeText(context, "시작일을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    ReservationStep.BIOMETRIC -> {
                        BiometricContent() // Transfer/Exchange와 동일 UI
                    }

                    ReservationStep.PIN -> { PinContent(viewModel = viewModel) }

                    ReservationStep.Finished -> {
                        val amountText = if (uiState.foreignValue > 0.0)
                            String.format("%,.2f", uiState.foreignValue)
                        else uiState.inputAmount

                        FinishedContent(
                            paddingValues = paddingValues,
                            currencyCode = uiState.toCurrencyCode,
                            currencyName = uiState.toCurrencyName,
                            targetRate = uiState.targetRate,
                            amount = amountText
                        )
                    }
                }
            }

            val (buttonText, isEnabled, onClick) = when (uiState.step) {
                ReservationStep.BoxSelection -> Triple(
                    "다음",
                    uiState.selectedBoxId > 0L,
                    { viewModel.goHome() }
                )
                ReservationStep.Home -> Triple(
                    "환전 예약하기",
                    true,
                    { viewModel.goCurrencySelectionIfBoxSelected() }
                )
                ReservationStep.CurrencySelection -> Triple(
                    "확인",
                    uiState.toCurrencyCode.isNotBlank(),
                    { viewModel.goRateInput() }
                )
                ReservationStep.RateInput -> Triple(
                    "다음으로",
                    uiState.targetRate != "0",
                    { viewModel.setTargetRate(uiState.targetRate) }
                )
                ReservationStep.AmountInput -> Triple(
                    "다음",
                    uiState.inputAmount != "0",
                    { viewModel.goPeriodSelection() }
                )
                ReservationStep.PeriodSelection -> Triple(
                    "예약하기",
                    uiState.periodStart.isNotEmpty() && uiState.periodEnd.isNotEmpty() && !uiState.isLoading,
                    { viewModel.onSubmitPressed() }
                )
                ReservationStep.BIOMETRIC -> Triple(
                    "PIN으로 인증하기",
                    true,
                    { viewModel.skipBiometrics() }
                )
                ReservationStep.PIN -> Triple(
                    "인증하기",
                    uiState.pin.length == 6 && !uiState.isPinLocked,
                    { viewModel.checkPin() } // ②에서 추가
                )
                ReservationStep.Finished -> Triple(
                    "확인",
                    true,
                    { viewModel.goHome() }
                )
            }

            if (uiState.step != ReservationStep.Home) { // 필요 시 Home에도 표시 가능
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onClick,
                    enabled = isEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(buttonText)
                }
            }
        }
    }
}

// --- 날짜 변환 헬퍼 함수 (파일 하단에 추가) ---
private fun String.toMillis(): Long {
    if (this.isEmpty()) return 0L
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        sdf.parse(this)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}

private fun Long.toFormattedString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(this))
}

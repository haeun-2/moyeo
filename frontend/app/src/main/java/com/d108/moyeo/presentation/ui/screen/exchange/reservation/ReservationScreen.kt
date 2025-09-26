package com.d108.moyeo.presentation.ui.screen.exchange.reservation

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    navController: NavController,
    viewModel: ReservationViewModel = hiltViewModel(),
    initialBoxId: Long? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val boxes by viewModel.boxes.collectAsState()

    LaunchedEffect(initialBoxId) { initialBoxId?.let(viewModel::setBox) }
    LaunchedEffect(Unit) { viewModel.refreshReservations() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.step == ReservationStep.Home) {
                            navController.popBackStack()
                        } else viewModel.toPrev()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로", tint = Color.Black)
                    }
                },
                title = {
                    Text(
                        when (uiState.step) {
                            ReservationStep.Home              -> "예약 환전"
                            ReservationStep.BoxSelection      -> "통장 선택"
                            ReservationStep.CurrencySelection -> "어떤 통화를 예약할까요?"
                            ReservationStep.RateInput         -> "예약 환율 입력"
                            ReservationStep.AmountInput       -> "예약 금액 입력"
                            ReservationStep.PeriodSelection   -> "예약 기간 선택"
                            ReservationStep.Finished     -> "완료"
                        }
                    )
                }
            )
        }
    ) { inner ->
        when (uiState.step) {
            ReservationStep.Home -> HomeContent(
                paddingValues = inner,
                isLoading = uiState.isLoading,
                reservations = uiState.reservations,
                onRefresh = { viewModel.refreshReservations() },
                onStartReservation = { viewModel.toNext() } // 홈 → 박스선택
            )
            // 1) 통장 선택: padding/버튼은 여기서 처리, 리스트는 TargetBoxContent 재활용
            ReservationStep.BoxSelection -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner)
                        .padding(20.dp)
                ) {
                    TargetBoxContent(
                        selectedBoxId = uiState.selectedBoxId,
                        onBoxSelect = viewModel::setBox,
                        boxes = boxes,
                        onNext = { if (uiState.selectedBoxId > 0L) viewModel.toNext() }
                    )

                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = { if (uiState.selectedBoxId > 0L) viewModel.toNext() },
                        enabled = uiState.selectedBoxId > 0L,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("다음")
                    }
                }
            }

            ReservationStep.CurrencySelection -> ChooseCurrencyContent(
                paddingValues = inner,
                selected = uiState.currencyCode.takeIf { it.isNotBlank() },
                onSelect = { code, name -> viewModel.setCurrency(code, name) },
                onNext = { if (uiState.currencyCode.isNotBlank()) viewModel.toNext() }
            )

            ReservationStep.RateInput -> RateInputContent(
                paddingValues = inner,
                currencyCode = uiState.currencyCode,
                currencyName = uiState.currencyName,
                input = uiState.inputRate,
                currentRate = uiState.currentRate,
                onInputChange = viewModel::setRateInput,
                onNext = { if (uiState.inputRate != "0") viewModel.toNext() }
            )

            ReservationStep.AmountInput -> AmountInputContent(
                paddingValues = inner,
                currencyCode = uiState.currencyCode,
                selectedTab = uiState.selectedTab,
                inputAmount = uiState.inputAmount,
                krwValue = uiState.krwValue,
                foreignValue = uiState.foreignValue,
                onTabChange = viewModel::setAmountTab,
                onInputChange = viewModel::setAmountInput,
                onNext = { if (uiState.inputAmount != "0") viewModel.toNext() }
            )

            ReservationStep.PeriodSelection -> PeriodSelectionContent(
                paddingValues = inner,
                startDate = uiState.startDate,
                endDate = uiState.endDate,
                onStartChange = viewModel::setPeriodStart,
                onEndChange = viewModel::setPeriodEnd,
                isLoading = uiState.isLoading,
                onSubmit = {
                    viewModel.createReservation(
                        onSuccess = { viewModel.toNext() },
                        onError = { /* TODO: 토스트/스낵 */ }
                    )
                }
            )

            ReservationStep.Finished -> FinishedContent(
                paddingValues = inner,
                currencyCode = uiState.currencyCode,
                currencyName = uiState.currencyName,
                targetRate = uiState.inputRate,
                amount = if (uiState.selectedTab == "KRW")
                    uiState.krwValue.toLong().toString() else uiState.inputAmount,
                onDone = {
                    viewModel.resetToFirst()
                    }
            )
        }
    }
}

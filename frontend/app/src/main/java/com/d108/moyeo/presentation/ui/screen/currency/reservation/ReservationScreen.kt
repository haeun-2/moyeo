package com.d108.moyeo.presentation.ui.screen.currency.reservation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.d108.moyeo.presentation.navigation.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    navController: NavController,
    viewModel: ReservationViewModel = hiltViewModel(),
    initialBoxId: Long? = null,
    entry: String = "home"
) {
    val ui by viewModel.ui.collectAsState()
    val boxes by viewModel.boxes.collectAsState(initial = emptyList())

    LaunchedEffect(initialBoxId, entry) {
        if (entry.equals("currency", ignoreCase = true)) {
            initialBoxId?.takeIf { it > 0 }?.let(viewModel::startFromCurrency)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("예약 환전") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.goPrev()
                        if (ui.step == ReservationStep.Home) navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->

        when (ui.step) {

            ReservationStep.BoxSelection -> {
                TargetBoxContent(
                    selectedBoxId = ui.selectedBoxId,
                    onBoxSelect = viewModel::selectBox,
                    boxes = boxes,
                    onNext = viewModel::goHome
                )
            }

            ReservationStep.Home -> {
                HomeContent(
                    paddingValues = paddingValues,
                    isLoading = ui.isLoading,
                    reservations = ui.reservations.map {
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
                    selected = ui.toCurrencyCode,
                    onSelect = { code, _ -> viewModel.selectCurrency(code) },
                    onNext = viewModel::goRateInput // 선택 후 '확인'을 눌러도 진행 가능
                )
            }

            ReservationStep.RateInput -> {
                RateInputContent(
                    paddingValues = paddingValues,
                    currencyCode = ui.toCurrencyCode,
                    currencyName = ui.toCurrencyName,
                    input = ui.targetRate,
                    currentRate = ui.currentRate, // 없으면 빈 문자열로 표시
                    onInputChange = viewModel::changeTargetRate,
                    onNext = { viewModel.setTargetRate(ui.targetRate) } // 입력 고정 후 다음
                )
            }

            ReservationStep.AmountInput -> {
                AmountInputContent(
                    paddingValues = paddingValues,
                    currencyCode = ui.toCurrencyCode.ifBlank { "—" },
                    selectedTab = ui.selectedTab,          // "KRW" or toCurrencyCode
                    inputAmount = ui.inputAmount,
                    krwValue = ui.krwValue,
                    foreignValue = ui.foreignValue,
                    onTabChange = viewModel::changeAmountTab,
                    onInputChange = viewModel::changeAmountInput,
                    onNext = viewModel::goPeriodSelection
                )
            }

            ReservationStep.PeriodSelection -> {
                PeriodSelectionContent(
                    paddingValues = paddingValues,
                    startDate = ui.periodStart,
                    endDate = ui.periodEnd,
                    onStartChange = viewModel::changePeriodStart,
                    onEndChange = viewModel::changePeriodEnd,
                    isLoading = ui.isLoading,
                    onSubmit = { viewModel.setPeriodAndSubmit(ui.periodStart, ui.periodEnd) }
                )
            }

            ReservationStep.Finished -> {
                // 완료 요약 금액은 외화 기준(가능하면 foreignValue 사용)
                val amountText = if (ui.foreignValue > 0.0)
                    String.format("%,.2f", ui.foreignValue)
                else ui.inputAmount

                FinishedContent(
                    paddingValues = paddingValues,
                    currencyCode = ui.toCurrencyCode,
                    currencyName = ui.toCurrencyName,
                    targetRate = ui.targetRate,
                    amount = amountText,
                    onDone = {
                        navController.navigate(AppScreen.Home.route) {
                            // 그래프의 시작 목적지까지 popUp (스택 정리)
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // 이미 홈이 최상단이면 재생성 방지
                            launchSingleTop = true
                            // 기존 상태 복원(탭/스크롤 위치 등)
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

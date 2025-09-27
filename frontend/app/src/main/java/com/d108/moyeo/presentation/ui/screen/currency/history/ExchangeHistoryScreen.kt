import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.ui.component.exchange.history.ExchangeHistoryControlPanel
import com.d108.moyeo.presentation.ui.component.exchange.history.ExchangeHistorySyncedCharts
import com.d108.moyeo.presentation.ui.screen.currency.history.ExchangeHistoryViewModel

@Composable
fun ExchangeHistoryScreen(  // TODO: 이거 tradeMode 받아오도록
    navController: NavController,
    currencyCode: String, // NavHost에서 넘겨준 파라미터
    currencyName: String, // NavHost에서 넘겨준 파라미터
    tradeMode: String = "charge",         // NavHost에서 넘겨준 파라미터
    viewModel: ExchangeHistoryViewModel = hiltViewModel()
) {
    // ViewModel의 UI 상태를 관찰합니다.
    val uiState by viewModel.uiState.collectAsState()

    // 화면이 처음 그려질 때 데이터 로딩을 시작합니다.
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchChartData(
            currencyCode = currencyCode,
            currencyName = currencyName,
            tradeMode = tradeMode, // TODO: 이거 받아오도록
            unit = "10m" // 초기 로딩 시 사용할 시간 단위,
        )
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 상단 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "뒤로가기")
                }
                // uiState의 currencyName을 사용해 제목을 표시합니다.
                Text(
                    text = uiState.currencyName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(48.dp)) // IconButton과 공간 대칭
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 로딩 및 에러 상태에 따라 다른 UI를 보여줍니다.
            if (uiState.isLoading) {
                // 로딩 중일 때
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null) {
                // 에러 발생 시
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "오류가 발생했습니다: ${uiState.errorMessage}")
                }
            } else {
                // 데이터 로딩 성공 시 메인 UI
                Column {
                    // [3단계]에서 만든 컨트롤 패널 조립
                    ExchangeHistoryControlPanel(
                        tradeMode = uiState.tradeMode,
                        selectedTimeUnit = uiState.selectedTimeUnit,
                        onTradeModeChange = viewModel::setTradeMode,
                        onTimeUnitChange = viewModel::setSelectedTimeUnit
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // [4단계]에서 만든 동기화 차트 조립
                    ExchangeHistorySyncedCharts(
                        rateData = uiState.exchangeRateHistoryData,
                        volumeData = uiState.exchangeVolumeHistoryData,
                        tradeMode = uiState.tradeMode
                    )
                }
            }
        }
    }
}
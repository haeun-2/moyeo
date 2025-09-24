import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.ui.screen.exchange.history.ExchangeHistoryViewModel

@Composable
fun ExchangeHistoryScreen(
    navController: NavController,
    currencyCode: String, // NavHost에서 넘겨준 파라미터
    currencyName: String, // NavHost에서 넘겨준 파라미터
    mode: String,         // NavHost에서 넘겨준 파라미터
    viewModel: ExchangeHistoryViewModel = hiltViewModel()
) {
    // ViewModel의 UI 상태를 관찰합니다.
    val uiState by viewModel.uiState.collectAsState()

    // 화면이 처음 그려질 때 데이터 로딩을 시작합니다.
    LaunchedEffect(key1 = currencyCode) {
        viewModel.fetchChartData(
            currencyCode = currencyCode,
            currencyName = currencyName,
            unit = "10m"
        )
    }


    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // TODO: 차트와 컨트롤 버튼 UI 구현
        Text(text = "${uiState.currencyName} (${uiState.currencyCode})의 환율 히스토리")
        // ...
    }
}
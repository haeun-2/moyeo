import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.presentation.navigation.AppScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. 목적지를 나타내는 Sealed Class 또는 Enum 정의
sealed class StartDestination(val route: String) {
    object Home : StartDestination(AppScreen.Home.route)  // 로그인 된 상태
    object First : StartDestination(AppScreen.First.route) // 로그아웃 된 상태
}

class SplashScreenViewModel : ViewModel() {

    // 2. 데이터 로딩 완료 여부
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    // 3. 최종 목적지 상태
    private val _startDestination = MutableStateFlow<StartDestination>(StartDestination.First)
    val startDestination = _startDestination.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // 여기에 실제 로그인 상태를 확인하는 로직을 넣습니다.
            // 예: val userToken = dataStore.getToken()
            val isLoggedIn = checkUserLoginStatus() // 이 함수는 SharedPreferences, DataStore 등에서 토큰을 확인하는 가상 함수입니다.

            if (isLoggedIn) {
                _startDestination.value = StartDestination.Home
            } else {
                _startDestination.value = StartDestination.First
            }

            // 모든 확인이 끝나면 isReady를 true로 변경하여 스플래시를 닫도록 신호를 보냅니다.
            _isReady.value = true
        }
    }

    // 실제로는 DataStore나 SharedPreferences를 확인해야 합니다.
    private suspend fun checkUserLoginStatus(): Boolean {
        delay(300L) // 가상 네트워크 딜레이
        return false // 테스트를 위해 '로그인 안 됨'으로 설정
    }
}
package com.d108.moyeo.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.d108.moyeo.R

sealed class AppScreen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val iconResId: Int? = null
) {

    // ImageVector가 있으면 바텀 내비 클릭해서 보이는 화면
    // ImageVector가 없으면 스크린

    object Dummy: AppScreen(route = "dummy", title = "Dummy")

    // 로그아웃 또는 앱 최초 설치
    object First: AppScreen(route = "first", title = "First")


    // 회원가입 관련
    object SignUp : AppScreen(route = "signup", title = "SignUp")

    // 로그인 관련
    object Login: AppScreen(route = "login", title = "Login")


    // 홈 스크린 관련
    object Home: AppScreen(route = "home", title = "홈", icon = Icons.Default.Home)
    object Notification: AppScreen(route = "notification", title = "알림")

    // 마이월렛에 파라미터를 타고 넘어가는 게 맞는 방향일까?
    // 이 주석 절대 지우지 말 것.
    object MyWallet: AppScreen(route = "my_wallet", title = "내 지갑")

    // 월렛에서 상세로 들어가는 화면
    object MyWalletDetail : AppScreen(route = "my_wallet_detail/{transactionId}", title = "월렛 상세")

    // 홈에서 모여 박스를 클릭했을 때 넘어오는 화면
    object MyBox : AppScreen(route = "my_box/{boxId}/{bgColor}", title = "내 박스")

    // 모여 박스 화면에서 거래 내역을 클릭했을 때 넘어오는 화면
    object MyBoxDetail : AppScreen(route = "my_box_detail/{transactionId}", title = "내 박스 상세")

    // 이체 또는 보내기 버튼을 클릭됐을 때 넘어갈 화면
    object Sending : AppScreen(route = "sending/{currencyId}", title = "보내기") {
        /**
         * '이체' 화면으로 이동할 때 사용할 실제 경로를 만드는 함수입니다.
         * 이렇게 하면 navigate 호출 시 오타를 방지할 수 있어 안전합니다.
         * 사용 예: AppScreen.Sending.createRoute("KRW") -> "sending/KRW"
         */
        fun createRoute(currencyId: String) = "sending/$currencyId"
    }

    // 충전화 관련된 화면
    object Charging: AppScreen(route = "charging", title = "충전")



    // 환율 스크린 관련
    object Exchange: AppScreen(route = "exchange", title = "환율", iconResId = R.drawable.outline_currency_exchange_24)

    object ExchangeKeypadCharge : AppScreen(route = "exchange_keypad/charge", title = "충전하기", icon= null, iconResId = null)
    object ExchangeKeypadRefund : AppScreen(route = "exchange_keypad/refund", title = "돌려받기", icon= null, iconResId = null)


    // QR 스크린 관련
    object QR: AppScreen(route = "qr", title = "QR", iconResId = R.drawable.outline_qr_code_24)
    object QRBoxes: AppScreen(route = "qr_boxes", title = "QR 박스")


    // 통장 히스토리 관련
    object History: AppScreen(route = "history", title = "기록", icon = Icons.Default.AccountBox)


    // 삼점바 관련
    object More: AppScreen(route = "more", title = "메뉴", iconResId = R.drawable.outline_more_horiz_24)
    object ConnectedAccountSettings : AppScreen(route = "connected_account_settings", title = "연결 계좌 설정")
    object Notices : AppScreen(route = "notices", title = "공지사항")
    object ChangePassword : AppScreen(route = "change_password", title = "비밀번호 변경")
    object FAQ: AppScreen(route = "faq", title = "자주 묻는 질문")
    object ChatConsultation: AppScreen(route = "chat_consultation", title = "채팅 상담")

    object MyConsultation : AppScreen(route = "my_consultation", title = "내 상담 이력")

    // '내 문의 내역'의 상세 화면 경로. {consultationId} 부분이 파라미터
    object ConsultationDetail: AppScreen(route = "consultation_detail/{consultationId}", title = "상담 내용 상세")


}
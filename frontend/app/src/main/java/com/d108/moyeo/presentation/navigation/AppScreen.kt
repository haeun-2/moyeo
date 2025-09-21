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
    object MyWallet: AppScreen(route = "my_wallet/{boxId}/{currencyCode}", title = "내 지갑") {
        fun createRoute(boxId: Long, currencyCode: String): String {
            return "my_wallet/$boxId/$currencyCode"
        }
    }

    // 월렛에서 상세로 들어가는 화면
    object MyWalletDetail : AppScreen(route = "my_wallet_detail/{historyId}?transactionJson={transactionJson}", title = "월렛 상세") {
        fun createRoute(historyId: Long, transactionJson: String): String {
            val encodedJson = java.net.URLEncoder.encode(transactionJson, "UTF-8")
            return "my_wallet_detail/$historyId?transactionJson=$encodedJson"
        }
    }

    // 홈에서 모여 박스를 클릭했을 때 넘어오는 화면
    object MyBox : AppScreen(route = "my_box/{boxId}/{bgColor}", title = "내 박스")

    // 모여 박스 화면에서 거래 내역을 클릭했을 때 넘어오는 화면
    object MyBoxDetail : AppScreen(route = "my_box_detail/{transactionId}", title = "내 박스 상세")

    // 이체 또는 보내기 버튼을 클릭됐을 때 넘어갈 화면
    object Transfer : AppScreen(
        route = "transfer?mode={mode}&targetBoxId={targetBoxId}&currencyId={currencyId}",
        title = "보내기"
    ) {
        /**
         * '이체' 화면으로 이동할 때 사용할 실제 경로를 만드는 함수입니다.
         * 이렇게 하면 navigate 호출 시 오타를 방지할 수 있어 안전합니다.
         * 사용 예: AppScreen.Transfer.createRoute("KRW") -> "transfer/KRW"
         */
        fun createRouteForTransfer(currencyId: String? = null): String {
            val base = "transfer?mode=transfer"
            return if (!currencyId.isNullOrBlank()) "$base&currencyId=$currencyId" else base
        }
        fun createRouteForDeposit(boxId: Long, currencyId: String? = null): String {
            val base = "transfer?mode=deposit&targetBoxId=$boxId"
            return if (!currencyId.isNullOrBlank()) "$base&currencyId=$currencyId" else base
        }
    }

    // 충전화 관련된 화면
    object Charge: AppScreen(route = "charge", title = "충전")

    // 모으기와 관련된 화면
    object Collecting : AppScreen(route = "collecting/{boxId}?currencyCode={currencyCode}", title = "모으기") {
        fun createRoute(boxId: String, currencyCode: String? = null): String {
            return if (currencyCode != null) {
                "collecting/$boxId?currencyCode=$currencyCode"
            } else {
                "collecting/$boxId"
            }
        }
    }

    // 정산하기와 관련된 화면
    object Calculating: AppScreen(route="calculating/{boxId}?currencyCode={currencyCode}", title = "정산하기") {
        fun createRoute(boxId: String, currencyCode: String? = null): String {
            return if (currencyCode != null) {
                "calculating/$boxId?currencyCode=$currencyCode"
            } else {
                "calculating/$boxId"
            }
        }
    }

    // 환율 스크린 관련
    object Exchange: AppScreen(route = "exchange", title = "환율", iconResId = R.drawable.outline_currency_exchange_24)

    object ExchangeKeypadCharge : AppScreen(route = "exchange_keypad/charge", title = "충전하기", icon= null, iconResId = null)
    object ExchangeKeypadRefund : AppScreen(route = "exchange_keypad/refund", title = "돌려받기", icon= null, iconResId = null)


    // QR 스크린 관련
    object QR: AppScreen(route = "qr", title = "QR", iconResId = R.drawable.outline_qr_code_24)
    object QRBoxes: AppScreen(route = "qr_boxes", title = "QR 박스")


    // 통장 히스토리 관련
    object History: AppScreen(route = "history", title = "기록", icon = Icons.Default.AccountBox)
    object HistoryBoxes : AppScreen(route = "history_boxes", title = "기록 박스 선택")


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

    // 그룹 박스 추가 관련
    object CreateBox: AppScreen(route = "create_box", title = "그룹 박스 생성")
}
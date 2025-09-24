package com.d108.moyeo.presentation.navigation

import MyConsultationDetail
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.d108.moyeo.presentation.ui.screen.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.d108.moyeo.presentation.ui.screen.exchange.CurrencySelectionScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeAddScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeCompleteScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeHistoryScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeKeypadScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeReservationHomeScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ReservationCompleteScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeReservationScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ReservationAmountInputScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ReservationFinalCompleteScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ReservationPeriodSelectionScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ReservationRateInputScreen
import com.d108.moyeo.presentation.ui.screen.history.HistoryBoxesScreen
import com.d108.moyeo.presentation.ui.screen.history.HistoryScreen
import com.d108.moyeo.presentation.ui.screen.home.HomeScreen
import com.d108.moyeo.presentation.ui.screen.home.box.MyBoxScreen
import com.d108.moyeo.presentation.ui.screen.home.wallet.MyWalletDetailScreen
import com.d108.moyeo.presentation.ui.screen.home.wallet.MyWalletScreen
import com.d108.moyeo.presentation.ui.screen.home.NotificationScreen
import com.d108.moyeo.presentation.ui.screen.home.box.MyBoxDetailScreen
import com.d108.moyeo.presentation.ui.screen.home.box.calculate.CalculateScreen
import com.d108.moyeo.presentation.ui.screen.home.box.member.MemberScreen
//import com.d108.moyeo.presentation.ui.screen.home.box.collect.CollectScreen
import com.d108.moyeo.presentation.ui.screen.home.charge.ChargeScreen
import com.d108.moyeo.presentation.ui.screen.home.create.CreateBoxScreen
import com.d108.moyeo.presentation.ui.screen.home.join.JoinScreen
import com.d108.moyeo.presentation.ui.screen.home.transfer.TransferScreen
import com.d108.moyeo.presentation.ui.screen.login.LoginScreen
import com.d108.moyeo.presentation.ui.screen.more.ChangePasswordScreen
import com.d108.moyeo.presentation.ui.screen.more.ChatConsultationScreen
import com.d108.moyeo.presentation.ui.screen.more.ConnectedAccountSettingsScreen
import com.d108.moyeo.presentation.ui.screen.more.FAQScreen
import com.d108.moyeo.presentation.ui.screen.more.MoreScreen
import com.d108.moyeo.presentation.ui.screen.more.MyConsultationScreen
import com.d108.moyeo.presentation.ui.screen.more.NoticesScreen
import com.d108.moyeo.presentation.ui.screen.qr.QRBoxesScreen
import com.d108.moyeo.presentation.ui.screen.qr.QRScreen
import com.d108.moyeo.presentation.ui.screen.signup.SignUpScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier,
    startDestination: String = AppScreen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(AppScreen.First.route) {
            FirstScreen(navController = navController)
        }

        composable(AppScreen.SignUp.route) {
            SignUpScreen(navController = navController)
        }

        composable(AppScreen.Login.route) {
            LoginScreen(navController = navController)
        }

        // 홈 화면
        composable(AppScreen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(AppScreen.Notification.route) {
            NotificationScreen(navController = navController)
        }

        composable(AppScreen.CreateBox.route) {
            CreateBoxScreen(
                onBackClick = { navController.popBackStack() },
                onFinishClick = {
                    // 결과 화면의 "닫기" 동작: 홈으로 복귀
                    navController.popBackStack(AppScreen.Home.route, inclusive = false)
                }
            )
        }

        composable(
            route = AppScreen.MyWallet.route,
            arguments = listOf(
                navArgument("boxId") { type = NavType.LongType },
                navArgument("currencyCode") { type = NavType.StringType }
            )
        ) {
            // MyWalletViewModel은 hiltViewModel()을 통해 SavedStateHandle을 주입받아
            // "boxId"와 "currencyCode"를 꺼내 쓸 수 있음
            MyWalletScreen(navController = navController)
        }

        composable(
            route = AppScreen.MyWalletDetail.route,
            // 경로에서 "transactionId"를 어떤 타입으로 받을지 정의합니다.
            arguments = listOf(
                navArgument("boxId") { type = NavType.LongType },
                navArgument("historyId") { type = NavType.LongType },
                navArgument("transactionJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            MyWalletDetailScreen(navController = navController)
        }

        // 모여 박스에서 클릭
        composable(
            route = AppScreen.MyBox.route,
            arguments = listOf(
                navArgument("boxId") { type = NavType.LongType },
            )
        ) { backStackEntry ->
            MyBoxScreen(navController = navController)
        }

        composable(
            route = AppScreen.Member.route,
            arguments = listOf(
                navArgument("boxId") { type = NavType.LongType }
            )
        ) {
            MemberScreen(
                navController = navController
            )
        }

        composable(
            route = AppScreen.MyBoxDetail.route,
            arguments = listOf(
                navArgument("boxId") { type = NavType.LongType },
                navArgument("historyId") { type = NavType.LongType },
                navArgument("transactionJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            MyBoxDetailScreen(navController = navController)
        }

        // 모여박스로 돈 보내는 화면
        composable(
            route = AppScreen.Transfer.route,
            // 1단계에서 정의한 {currencyId}가 어떤 타입인지 알려줍니다.
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType; defaultValue = "TRANSFER" },
                navArgument("targetBoxId") { type = NavType.LongType; defaultValue = -1L },
                navArgument("currencyId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) {
            // TransferScreen을 호출
            // TransferViewModel은 hiltViewModel() 또는 viewModel()을 통해
            // 자동으로 SavedStateHandle을 주입받아 currencyId를 꺼내 사용
            TransferScreen(navController = navController)
        }

        // 페이머니 충전 화면
        composable(route = AppScreen.Charge.route) {
            ChargeScreen(navController = navController)
        }

        // 정산하기 화면
        composable(
            route = AppScreen.Calculate.route,
            arguments = listOf(
                navArgument("boxId") { type = NavType.LongType },
                // currencyCode는 null일 수 있는 선택적 인자임을 정의
                navArgument("currencyCode") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "KRW"
                }
            )
        ) { backStackEntry ->
            // ViewModel에서 SavedStateHandle을 통해 boxId를 직접 받으므로,
            CalculateScreen(navController = navController)
        }

        // 환율 화면
        composable(AppScreen.Exchange.route) {
            ExchangeScreen(navController = navController)
        }

        composable(
            "exchange_keypad/{mode}?currencyCode={currencyCode}&currencyName={currencyName}",
            arguments= listOf(
                navArgument("mode") {type = NavType.StringType},
                navArgument("currencyCode") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("currencyName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )) { backStackEntry ->
            ExchangeKeypadScreen(
                navController = navController,
                mode = backStackEntry.arguments?.getString("mode") ?: "charge",
                currencyCode = backStackEntry.arguments?.getString("currencyCode"),
                currencyName = backStackEntry.arguments?.getString("currencyName")
            )
        }

        // 예약환전 홈
        composable("exchange_reservation_home") {
            ExchangeReservationHomeScreen(navController = navController)
        }

// 희망환율 입력
        composable(
            route = "reservation_rate_input/{currencyCode}/{currencyName}",
            arguments = listOf(
                navArgument("currencyCode") { type = NavType.StringType },
                navArgument("currencyName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: ""
            val currencyName = backStackEntry.arguments?.getString("currencyName") ?: ""

            ReservationRateInputScreen(
                navController = navController,
                currencyCode = currencyCode,
                currencyName = currencyName
            )
        }

// 환전할 금액 입력
        composable(
            route = "reservation_amount_input/{currencyCode}/{currencyName}/{targetRate}",
            arguments = listOf(
                navArgument("currencyCode") { type = NavType.StringType },
                navArgument("currencyName") { type = NavType.StringType },
                navArgument("targetRate") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: ""
            val currencyName = backStackEntry.arguments?.getString("currencyName") ?: ""
            val targetRate = backStackEntry.arguments?.getLong("targetRate") ?: 0L

            ReservationAmountInputScreen(
                navController = navController,
                currencyCode = currencyCode,
                currencyName = currencyName,
                targetRate = targetRate
            )
        }

// 예약기간 선택
        composable(
            route = "reservation_period_selection/{currencyCode}/{currencyName}/{targetRate}/{amount}",
            arguments = listOf(
                navArgument("currencyCode") { type = NavType.StringType },
                navArgument("currencyName") { type = NavType.StringType },
                navArgument("targetRate") { type = NavType.LongType },
                navArgument("amount") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: ""
            val currencyName = backStackEntry.arguments?.getString("currencyName") ?: ""
            val targetRate = backStackEntry.arguments?.getLong ("targetRate") ?: 0L
            val amount = backStackEntry.arguments?.getString("amount") ?: ""

            ReservationPeriodSelectionScreen(
                navController = navController,
                currencyCode = currencyCode,
                currencyName = currencyName,
                targetRate = targetRate,
                amount = amount
            )
        }

// 예약환전 완료
        composable(
            route = "reservation_complete/{currencyCode}/{currencyName}/{targetRate}/{amount}/{startDate}/{endDate}",
            arguments = listOf(
                navArgument("currencyCode") { type = NavType.StringType },
                navArgument("currencyName") { type = NavType.StringType },
                navArgument("targetRate") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType },
                navArgument("startDate") { type = NavType.StringType },
                navArgument("endDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: ""
            val currencyName = backStackEntry.arguments?.getString("currencyName") ?: ""
            val targetRate = backStackEntry.arguments?.getString("targetRate") ?: ""
            val amount = backStackEntry.arguments?.getString("amount") ?: ""
            val startDate = backStackEntry.arguments?.getString("startDate") ?: ""
            val endDate = backStackEntry.arguments?.getString("endDate") ?: ""

            ReservationFinalCompleteScreen(
                navController = navController,
                currencyCode = currencyCode,
                currencyName = currencyName,
                targetRate = targetRate,
                amount = amount,
                startDate = startDate,
                endDate = endDate
            )
        }

        // 환율의 추가 버튼을 누르면 이동
        composable("exchange_add") {
            ExchangeAddScreen(navController)
        }

        // 예약 환전
        composable("currency_selection") {
            CurrencySelectionScreen(navController)
        }

        // 국가별 환전
        composable(
            route = "exchange_reservation/{currencyCode}/{currencyName}",
            arguments = listOf(
                navArgument("currencyCode") { type = NavType.StringType },
                navArgument("currencyName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: ""
            val currencyName = backStackEntry.arguments?.getString("currencyName") ?: ""
            ExchangeReservationScreen(
                navController = navController,
                currencyCode = currencyCode,
                currencyName = currencyName
            )
        }
        // 예약환전 완료
        // Navigation에 추가
        composable(
            route = "reservation_complete/{currencyCode}/{currencyName}/{foreignAmount}/{krwAmount}",
            arguments = listOf(
                navArgument("currencyCode") { type = NavType.StringType },
                navArgument("currencyName") { type = NavType.StringType },
                navArgument("foreignAmount") { type = NavType.StringType },
                navArgument("krwAmount") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: ""
            val currencyName = backStackEntry.arguments?.getString("currencyName") ?: ""
            val foreignAmount = backStackEntry.arguments?.getString("foreignAmount") ?: ""
            val krwAmount = backStackEntry.arguments?.getString("krwAmount") ?: ""

            ReservationCompleteScreen(
                navController = navController,
                currencyCode = currencyCode,
                currencyName = currencyName,
                foreignAmount = foreignAmount,
                krwAmount = krwAmount
            )
        }

        // 환전 완료 화면
        composable(
            route = "exchange_complete/{mode}/{amount}/{currencyUnit}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType },
                navArgument("currencyUnit") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "charge"
            val amount = backStackEntry.arguments?.getString("amount") ?: "0"
            val currencyUnit = backStackEntry.arguments?.getString("currencyUnit") ?: "JPY"

            ExchangeCompleteScreen(
                navController = navController,
                mode = mode,
                amount = amount,
                currencyUnit = currencyUnit
            )
        }

        // 환율 히스토리
        composable(
            route = "exchange_history/{currencyCode}/{currencyName}/{mode}",
            arguments = listOf(
                navArgument("currencyCode") { type = NavType.StringType },
                navArgument("currencyName") { type = NavType.StringType },
                navArgument("mode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: ""
            val currencyName = backStackEntry.arguments?.getString("currencyName") ?: ""
            val mode = backStackEntry.arguments?.getString("mode") ?: "charge"

            ExchangeHistoryScreen(
                navController = navController,
                currencyCode = currencyCode,
                currencyName = currencyName,
                mode = mode
            )
        }


        // QR 화면
        composable(AppScreen.QR.route) {
            QRScreen(navController = navController)
        }

        composable(AppScreen.QRBoxes.route) {
            QRBoxesScreen(navController = navController)
        }


        // 기록 화면
        composable(AppScreen.History.route) {
            HistoryScreen(navController = navController)
        }

        composable(AppScreen.HistoryBoxes.route) {
            HistoryBoxesScreen(navController = navController)
        }


        // 더보기 화면
        composable(AppScreen.More.route) {
            MoreScreen(navController = navController)
        }

        composable(AppScreen.ConnectedAccountSettings.route) {
            ConnectedAccountSettingsScreen(navController = navController)
        }

        composable(AppScreen.Notices.route) {
            NoticesScreen(navController = navController)
        }

        composable(AppScreen.ChangePassword.route) {
            ChangePasswordScreen(navController = navController)
        }

        composable(AppScreen.FAQ.route) {
            FAQScreen(navController = navController)
        }

        composable(AppScreen.ChatConsultation.route) {
            ChatConsultationScreen(navController = navController)
        }

        composable(AppScreen.MyConsultation.route) {
            MyConsultationScreen(navController = navController)
        }

        composable(
            route = AppScreen.ConsultationDetail.route,
            // "consultationId"라는 이름의 파라미터를 받으며, 이 값은 정수(Int) 타입임을 정의합니다.
            arguments = listOf(navArgument("consultationId") { type = NavType.IntType })
        ) { backStackEntry ->
            // URL 경로로부터 "consultationId" 값을 안전하게 추출합니다.
            val consultationId = backStackEntry.arguments?.getInt("consultationId") ?: -1
            MyConsultationDetail(navController = navController, consultationId = consultationId)
        }

        composable(
            route = "join?code={code}",
            arguments = listOf(
                navArgument("code") {
                    type = NavType.StringType
                    defaultValue = ""   // 빈 문자열도 허용
                    nullable = true
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "moyeo://invite?code={code}" },
                navDeepLink { uriPattern = "http://j13d108.p.ssafy.io:8080/invite/{code}" },
                navDeepLink { uriPattern = "https://j13d108.p.ssafy.io/invite/{code}" }
            )
        ) { backStackEntry ->
            val inviteCode = backStackEntry.arguments?.getString("code")
                ?.takeIf { !it.isNullOrBlank() }

            JoinScreen(
                codeFromDeepLink = inviteCode, // <- JoinViewModel.startAuthFlow()로 전달됨
                onFinished = {
                    // 합류 완료 후 뒤로 가기 또는 홈으로
                     navController.navigate(AppScreen.Home.route) { popUpTo(0) }
                }
            )
        }
    }
}
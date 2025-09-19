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
import com.d108.moyeo.presentation.ui.screen.exchange.CurrencySelectionScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeAddScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeCompleteScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeHistoryScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeKeypadScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ReservationCompleteScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeReservationScreen
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeScreen
import com.d108.moyeo.presentation.ui.screen.history.HistoryBoxesScreen
import com.d108.moyeo.presentation.ui.screen.history.HistoryScreen
import com.d108.moyeo.presentation.ui.screen.home.HomeScreen
import com.d108.moyeo.presentation.ui.screen.home.box.MyBoxScreen
import com.d108.moyeo.presentation.ui.screen.home.wallet.MyWalletDetailScreen
import com.d108.moyeo.presentation.ui.screen.home.wallet.MyWalletScreen
import com.d108.moyeo.presentation.ui.screen.home.NotificationScreen
import com.d108.moyeo.presentation.ui.screen.home.box.MyBoxDetailScreen
import com.d108.moyeo.presentation.ui.screen.home.box.calculating.CalculatingScreen
import com.d108.moyeo.presentation.ui.screen.home.box.collecting.CollectingScreen
import com.d108.moyeo.presentation.ui.screen.home.charge.ChargingScreen
import com.d108.moyeo.presentation.ui.screen.home.create.CreateBoxScreen
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

        // 후에 마이 월렛으로 어떤 화폐를 타고 들어왔는지 파라미터 도입...아니다 지금 할까?
        // 고민해보자. 이 주석 절대 지우지 말 것.
        composable(AppScreen.MyWallet.route) {
            MyWalletScreen(navController = navController)
        }

        composable(
            route = AppScreen.MyWalletDetail.route,
            // 경로에서 "transactionId"를 어떤 타입으로 받을지 정의합니다.
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            // 뒤로가기 스택에서 "transactionId" 값을 꺼냅니다.
            val transactionId = backStackEntry.arguments?.getString("transactionId")
            if (transactionId != null) {
                MyWalletDetailScreen(
                    navController = navController,
                    transactionId = transactionId // 상세 화면에 ID를 전달합니다.
                )
            }
        }

        // 모여 박스에서 클릭
        composable(
            route = AppScreen.MyBox.route,
            arguments = listOf(navArgument("boxId") { type = NavType.StringType },
                navArgument("bgColor") { type = NavType.IntType }  // Color는 Int로 전달)
            )
        ) { backStackEntry ->
            val boxId = backStackEntry.arguments?.getString("boxId")
            val bgColor = backStackEntry.arguments?.getInt("bgColor")
            if (boxId != null && bgColor != null ) {
                MyBoxScreen(navController = navController, boxId = boxId, bgColor = bgColor)
            }
        }

        composable(
            route = AppScreen.MyBoxDetail.route,
            // 경로에서 "transactionId"를 어떤 타입으로 받을지 정의합니다.
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            // 뒤로가기 스택에서 "transactionId" 값을 꺼냅니다.
            val transactionId = backStackEntry.arguments?.getString("transactionId")
            if (transactionId != null) {
                MyBoxDetailScreen(
                    navController = navController,
                    transactionId = transactionId // 상세 화면에 ID를 전달합니다.
                )
            }
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
        composable(route = AppScreen.Charging.route) {
            ChargingScreen(navController = navController)
        }

        // 모으기 화면
        composable(
            route = AppScreen.Collecting.route,
            arguments = listOf(
                navArgument("boxId") { type = NavType.StringType },
                // currencyCode는 null일 수 있는 선택적 인자임을 정의
                navArgument("currencyCode") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "KRW"
                }
            )
        ) { backStackEntry ->
            // CollectingScreen은 ViewModel에서 SavedStateHandle을 통해 boxId를 직접 받으므로,
            // 여기서 따로 전달해 줄 필요는 없습니다.
            CollectingScreen(navController = navController)
        }

        // 정산하기 화면
        composable(
            route = AppScreen.Calculating.route,
            arguments = listOf(
                navArgument("boxId") { type = NavType.StringType },
                // currencyCode는 null일 수 있는 선택적 인자임을 정의
                navArgument("currencyCode") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "KRW"
                }
            )
        ) { backStackEntry ->
            // ViewModel에서 SavedStateHandle을 통해 boxId를 직접 받으므로,
            //
            CalculatingScreen(navController = navController)
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
    }
}
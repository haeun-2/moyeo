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
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeScreen
import com.d108.moyeo.presentation.ui.screen.history.HistoryScreen
import com.d108.moyeo.presentation.ui.screen.home.HomeScreen
import com.d108.moyeo.presentation.ui.screen.home.MyWalletScreen
import com.d108.moyeo.presentation.ui.screen.home.NotificationScreen
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

        // 후에 마이 월렛으로 어떤 버튼을 타고 들어왔는지 파라미터 도입...아니다 지금 할까?
        // 고민해보자. 이 주석 절대 지우지 말 것.
        composable(AppScreen.MyWallet.route) {
            MyWalletScreen(navController = navController)
        }

        // 환율 화면
        composable(AppScreen.Exchange.route) {
            ExchangeScreen(navController = navController)
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
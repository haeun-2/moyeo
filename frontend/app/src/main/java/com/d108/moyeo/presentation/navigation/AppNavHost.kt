package com.d108.moyeo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.d108.moyeo.presentation.ui.screen.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeScreen
import com.d108.moyeo.presentation.ui.screen.history.HistoryScreen
import com.d108.moyeo.presentation.ui.screen.home.HomeScreen
import com.d108.moyeo.presentation.ui.screen.home.HomeSecondScreen
import com.d108.moyeo.presentation.ui.screen.login.LoginScreen
import com.d108.moyeo.presentation.ui.screen.more.ChangePasswordScreen
import com.d108.moyeo.presentation.ui.screen.more.ChatConsultationScreen
import com.d108.moyeo.presentation.ui.screen.more.ConnectedAccountSettingsScreen
import com.d108.moyeo.presentation.ui.screen.more.FAQScreen
import com.d108.moyeo.presentation.ui.screen.more.MoreScreen
import com.d108.moyeo.presentation.ui.screen.more.MyConsultationScreen
import com.d108.moyeo.presentation.ui.screen.more.NoticesScreen
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

        composable(AppScreen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(AppScreen.HomeSecond.route) { // HomeSecondScreen 라우트 추가
            HomeSecondScreen(navController = navController)
        }

        composable(AppScreen.Exchange.route) {
            ExchangeScreen(navController = navController)
        }

        composable(AppScreen.QR.route) {
            QRScreen(navController = navController)
        }

        composable(AppScreen.History.route) {
            HistoryScreen(navController = navController)
        }

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

        composable(AppScreen.MyConsultation.route) {
            MyConsultationScreen(navController = navController)
        }

        composable(AppScreen.ChatConsultation.route) {
            ChatConsultationScreen(navController = navController)
        }
    }
}
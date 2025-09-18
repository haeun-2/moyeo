package com.d108.moyeo

import SplashScreenViewModel
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.d108.moyeo.presentation.navigation.AppNavHost
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.MoYeoTheme
import com.d108.moyeo.presentation.ui.component.MoyeoBottomNavigation
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlin.getValue

@AndroidEntryPoint  // MainActivity는 Hilt의 관리를 받음
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val viewModel: SplashScreenViewModel by viewModels()

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !viewModel.isReady.value
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoYeoTheme {
                val startDestination by viewModel.startDestination.collectAsState()
                val navController = rememberNavController()

                // 현재 화면의 경로를 가져오기 위해 NavBackStackEntry를 관찰
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // 바텀 네비게이션을 표시할 화면 경로 목록
                val screensWithBottomNav = listOf(
                    AppScreen.Home.route,
                    AppScreen.Exchange.route,
                    AppScreen.QR.route,
                    AppScreen.History.route,
                    AppScreen.More.route
                )


                // 바텀 네비게이션에 표시될 아이템 목록
                val bottomNavItems = listOf(
                    AppScreen.Home,
                    AppScreen.Exchange,
                    AppScreen.QR,
                    AppScreen.History,
                    AppScreen.More
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute in screensWithBottomNav) {
                            MoyeoBottomNavigation(
                                navController = navController,
                                items = bottomNavItems
                            )
                        }
                    }
                )
                { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        startDestination = startDestination.route  // 여기서 startDestination을 설정
                    )
                }
            }
        }
    }
}

@Composable
fun DummyButton(name: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(text = "Hello $name!")
    }
}


package com.d108.moyeo

import SplashScreenViewModel
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.d108.moyeo.presentation.navigation.AppNavHost
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.MoYeoTheme
import com.d108.moyeo.presentation.ui.component.MoyeoBottomNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint  // MainActivity는 Hilt의 관리를 받음
class MainActivity : FragmentActivity() {

    // ✨ 1. 권한 요청을 위한 런처 등록
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 사용자가 권한을 허용한 경우의 동작 (예: 로그 남기기)
            Log.d("MainActivity", "알림 권한이 허용되었습니다.")
        } else {
            // 사용자가 권한을 거부한 경우의 동작
            Log.d("MainActivity", "알림 권한이 거부되었습니다.")
        }
    }

    private fun askNotificationPermission() {
        // ✨ 2. Android 13 이상에서만 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val splashScreenViewModel: SplashScreenViewModel by viewModels()
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()

        installSplashScreen().apply {
            setKeepOnScreenCondition { !splashScreenViewModel.isReady.value }
        }

        enableEdgeToEdge()
        setContent {
            MoYeoTheme {
                val startDestination by splashScreenViewModel.startDestination.collectAsState()
                navController = rememberNavController()

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
                // ✅ NavController가 준비된 뒤 한 번만 딥링크 처리
                LaunchedEffect(Unit) {
                    navController.handleDeepLink(intent)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (::navController.isInitialized) {
            navController.handleDeepLink(intent)
        }
    }
}

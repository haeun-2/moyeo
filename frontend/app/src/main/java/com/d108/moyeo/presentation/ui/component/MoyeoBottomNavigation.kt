package com.d108.moyeo.presentation.ui.component

import android.media.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.d108.moyeo.presentation.navigation.AppScreen

@Composable
fun MoyeoBottomNavigation(
    navController: NavController,
    items: List<AppScreen>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { screen ->
            // 아이콘이 있는 화면만 바텀 네비게이션 아이템으로 생성
            if (screen.icon != null) { // screen.icon이 null이 아닐 때만 아이템 생성
                NavigationBarItem(
                    icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) }, // imageVector 사용
                    label = { Text(screen.title) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    alwaysShowLabel = true
                )
            }
        }
    }
}

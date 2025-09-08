// MoyeoBottomNavigation.kt
package com.d108.moyeo.presentation.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.surfaceLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoyeoBottomNavigation(
    navController: NavController,
    items: List<AppScreen>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    CompositionLocalProvider(
        LocalRippleConfiguration provides null
    ) {
        NavigationBar(
                containerColor = surfaceLight,
        ) {
            items.forEach { screen ->
                if (screen.icon != null || screen.iconResId != null) { // ← 리소스 아이콘도 허용
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        icon = {
                            when {
                                screen.iconResId != null -> {
                                    Icon(
                                        painter = painterResource(screen.iconResId),
                                        contentDescription = screen.title,
                                    )
                                }
                                screen.icon != null -> {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                    )
                                }
                            }
                        },
                        label = { Text(screen.title) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    }
}

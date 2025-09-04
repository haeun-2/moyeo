package com.d108.moyeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.d108.moyeo.presentation.navigation.AppNavHost
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.MoYeoTheme
import com.d108.moyeo.presentation.ui.component.MoyeoBottomNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoYeoTheme {
                val navController = rememberNavController()

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
                        MoyeoBottomNavigation(
                            navController = navController,
                            items = bottomNavItems,
                        )
                    }
                )
                { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        startDestination = AppScreen.Home.route
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


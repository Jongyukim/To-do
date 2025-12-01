// SmartTodoApp.kt
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun SmartTodoApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Onboarding) }
    val store = remember { TodoStore() }

    MaterialTheme {
        when (screen) {
            Screen.Onboarding -> OnboardingScreens(
                onSkip = { screen = Screen.Auth },
                onFinish = { screen = Screen.Auth }
            )

            Screen.Auth -> AuthScreen(
                onAuthenticated = { screen = Screen.Home },
                onForgotPassword = { /* 비밀번호 찾기 미구현 */ }
            )

            Screen.Home -> HomeScreen(
                store = store,
                onOpenCategory = { screen = Screen.Category },
                onOpenCalendar = { screen = Screen.Calendar },
                onOpenAlarm = { screen = Screen.Notifications },
                onOpenStats = { screen = Screen.Stats },
                onOpenSettings = { screen = Screen.Settings },
                onOpenProfile = { screen = Screen.Profile }
            )

            Screen.Calendar -> CalendarScreen(
                store = store,
                onBack = { screen = Screen.Home }
            )

            Screen.Notifications -> NotificationScreen(
                store = store,
                onBack = { screen = Screen.Home }
            )

            Screen.Category -> CategoryScreen(
                store = store,
                onBack = { screen = Screen.Home }
            )

            Screen.Stats -> StatisticsScreen(
                store = store,
                onBackHome = { screen = Screen.Home },
                onOpenSettings = { screen = Screen.Settings }
            )

            Screen.Settings -> SettingsScreen(
                onBack = { screen = Screen.Home },
                onOpenProfile = { screen = Screen.Profile },
                onLogout = { screen = Screen.Auth }
            )

            Screen.Profile -> ProfileScreen(
                store = store,
                onBack = { screen = Screen.Settings }
            )
        }
    }
}

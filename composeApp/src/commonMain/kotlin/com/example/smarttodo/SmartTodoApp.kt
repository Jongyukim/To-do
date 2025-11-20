@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

@Composable
fun SmartTodoApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Onboarding) }
    val notificationManager = remember { 
        try {
            getNotificationManager()
        } catch (e: Exception) {
            null // Desktop이나 초기화되지 않은 경우 null
        }
    }
    val store = remember { TodoStore(notificationManager) }

    // 모던하고 일관된 색상 팔레트
    val customColorScheme = lightColorScheme(
        primary = Color(0xFF6366F1), // 인디고 계열 - 더 모던한 느낌
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE0E7FF),
        onPrimaryContainer = Color(0xFF312E81),
        secondary = Color(0xFF10B981), // 에메랄드 그린
        onSecondary = Color.White,
        tertiary = Color(0xFFF59E0B), // 앰버
        onTertiary = Color.White,
        error = Color(0xFFEF4444),
        onError = Color.White,
        background = Color(0xFFFAFAFA),
        onBackground = Color(0xFF1F2937),
        surface = Color.White,
        onSurface = Color(0xFF1F2937),
        surfaceVariant = Color(0xFFF3F4F6),
        onSurfaceVariant = Color(0xFF6B7280)
    )

    MaterialTheme(colorScheme = customColorScheme) {
        when (screen) {
            Screen.Onboarding -> OnboardingScreens(
                onSkip = { screen = Screen.Auth }, onFinish = { screen = Screen.Auth }
            )

            Screen.Auth -> AuthScreen(
                onAuthenticated = { screen = Screen.Home }, onForgotPassword = {}
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

            Screen.Category -> CategoryScreen(store = store, onBack = { screen = Screen.Home })
            Screen.Calendar -> CalendarScreen(store = store, onBack = { screen = Screen.Home })
            Screen.Notifications -> NotificationScreen(
                store = store,
                onBack = { screen = Screen.Home })

            Screen.Stats -> StatisticsScreen(
                store = store,
                onBackHome = { screen = Screen.Home },
                onOpenSettings = { screen = Screen.Settings }
            )

            Screen.Settings -> SettingsScreen(
                onBack = { screen = Screen.Home },
                onOpenProfile = { screen = Screen.Profile }
            )

            Screen.Profile -> ProfileScreen(
                store = store,
                onBack = { screen = Screen.Settings }
            )
        }
    }
}
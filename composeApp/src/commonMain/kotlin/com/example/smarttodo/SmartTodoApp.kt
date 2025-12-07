@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.smarttodo.data.FirebaseRepository
import com.example.smarttodo.data.getFirebaseRepository

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
    // store는 이제 로컬 전용 기능(알림 등)에만 제한적으로 사용되거나, 점차 제거될 수 있습니다.
    val store = remember { TodoStore(notificationManager) }
    val firebaseRepository = remember { getFirebaseRepository() }
    val authManager = remember { getAuthManager() }

    // 앱 시작 시 로그인 상태 확인
    LaunchedEffect(Unit) {
        if (authManager.isLoggedIn()) {
            screen = Screen.Home
        }
    }

    val customColorScheme = lightColorScheme(
        primary = Color(0xFF6366F1),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE0E7FF),
        onPrimaryContainer = Color(0xFF312E81),
        secondary = Color(0xFF10B981),
        onSecondary = Color.White,
        tertiary = Color(0xFFF59E0B),
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
                onLogin = { email, password -> authManager.login(email, password) },
                onRegister = { email, password, displayName -> authManager.register(email, password, displayName) },
                onAuthenticationSuccess = { screen = Screen.Home },
                onForgotPassword = {}
            )

            Screen.Home -> HomeScreen(
                repository = firebaseRepository,
                onOpenCategory = { screen = Screen.Category },
                onOpenCalendar = { screen = Screen.Calendar },
                onOpenAlarm = { screen = Screen.Notifications },
                onOpenStats = { screen = Screen.Stats },
                onOpenSettings = { screen = Screen.Settings },
                onOpenProfile = { screen = Screen.Profile }
            )

            Screen.Category -> CategoryScreen(store = store, onBack = { screen = Screen.Home })

            Screen.Calendar -> CalendarScreen(
                repository = firebaseRepository,
                onBack = { screen = Screen.Home }
            )

            // [수정 완료] 알림 화면에 repository 연결
            Screen.Notifications -> NotificationScreen(
                store = store,
                repository = firebaseRepository, // 여기를 추가했습니다!
                onBack = { screen = Screen.Home }
            )

            Screen.Stats -> StatisticsScreen(
                repository = firebaseRepository,
                onBackHome = { screen = Screen.Home },
                onOpenSettings = { screen = Screen.Settings }
            )

            Screen.Settings -> SettingsScreen(
                onBack = { screen = Screen.Home },
                onOpenProfile = { screen = Screen.Profile },
                onLogout = {
                    authManager.signOut()
                    screen = Screen.Auth
                }
            )

            Screen.Profile -> ProfileScreen(
                store = store,
                onBack = { screen = Screen.Settings },
                authManager = authManager
            )
        }
    }
}
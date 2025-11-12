package com.example.smarttodo

sealed interface Screen {
    data object Onboarding : Screen
    data object Auth : Screen
    data object Home : Screen
    data object Calendar : Screen
    data object Notifications : Screen
    data object Category : Screen
    data object Stats : Screen
    data object Settings : Screen
    data object Profile : Screen
}

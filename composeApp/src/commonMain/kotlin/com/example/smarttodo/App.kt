package com.example.smarttodo

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun App() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        SmartTodoApp()
    }
}
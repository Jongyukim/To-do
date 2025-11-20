package com.example.smarttodo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // NotificationManager 초기화
        initNotificationManager(this)
        
        // 알림 클릭으로 시작된 경우 처리
        handleNotificationIntent(intent)

        setContent {
            App()
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }
    
    private fun handleNotificationIntent(intent: Intent) {
        val todoId = intent.getStringExtra("todoId")
        if (todoId != null) {
            // TODO: 특정 할 일로 이동하는 로직 구현
            // 현재는 앱만 열림
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenProfile: () -> Unit
) {
    var darkMode by remember { mutableStateOf(false) }
    var pushEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 계정
            SectionHeader("계정")
            Card(shape = MaterialTheme.shapes.extraLarge) {
                ListItem(
                    leadingContent = {
                        Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.primary)
                    },
                    headlineContent = { Text("프로필 보기", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("활동 통계 및 일정 확인") },
                    trailingContent = { Icon(Icons.Filled.ArrowForwardIos, null) },
                    modifier = Modifier.clickable(onClick = onOpenProfile)
                )
            }

            // 모양
            SectionHeader("모양")
            Card(shape = MaterialTheme.shapes.extraLarge) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.DarkMode, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("다크 모드", fontWeight = FontWeight.SemiBold)
                        Text("어두운 테마 사용", color = Color.Gray)
                    }
                    Switch(checked = darkMode, onCheckedChange = { darkMode = it })
                }
            }

            // 알림
            SectionHeader("알림")
            Card(shape = MaterialTheme.shapes.extraLarge) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Notifications, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("푸시 알림", fontWeight = FontWeight.SemiBold)
                        Text("할 일 알림 받기", color = Color.Gray)
                    }
                    Switch(checked = pushEnabled, onCheckedChange = { pushEnabled = it })
                }
            }

            // 앱 정보
            SectionHeader("앱 정보")
            Card(shape = MaterialTheme.shapes.extraLarge) {
                Column(Modifier.fillMaxWidth()) {
                    SettingLinkRow("앱 버전", "1.0.0")
                    Divider()
                    SettingLinkRow("개인정보 처리방침")
                    Divider()
                    SettingLinkRow("이용 약관")
                }
            }

            // 계정 - 로그아웃
            SectionHeader("계정")
            OutlinedButton(
                onClick = { /* TODO: 로그아웃 */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("로그아웃")
            }

            Spacer(Modifier.height(12.dp))
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("스마트 To-Do", color = Color.Gray)
                Text("© 2025 All rights reserved", color = Color.Gray)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun SettingLinkRow(title: String, subtitle: String? = null) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { if (subtitle != null) Text(subtitle, color = Color.Gray) },
        trailingContent = { Icon(Icons.Filled.ArrowForwardIos, null) }
    )
}
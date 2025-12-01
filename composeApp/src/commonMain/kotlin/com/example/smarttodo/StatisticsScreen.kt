@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatisticsScreen(
    store: TodoStore,
    onBackHome: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val total = store.items.size
    val done = store.items.count { it.done }
    val active = total - done
    val rate = if (total > 0) (done * 100 / total) else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("통계") },
                navigationIcon = {
                    IconButton(onClick = onBackHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "홈으로"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "설정"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "간단 통계",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "할 일 진행 상황을 한 눈에 확인하세요.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Assessment,
                    contentDescription = null
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("전체 할 일: ${total}개")
                    Text("완료: ${done}개")
                    Text("진행 중: ${active}개")
                    Text("완료율: ${rate}%")
                }
            }

            Text(
                text = "※ 이 앱에서 핵심 구현 범위는 로그인 / 회원가입 / 로그아웃입니다.\n" +
                        "   통계 화면은 최소한의 정보만 보여주는 단순 버전입니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

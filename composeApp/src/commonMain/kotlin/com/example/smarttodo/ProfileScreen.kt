// ProfileScreen.kt
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@Composable
fun ProfileScreen(
    store: TodoStore,
    onBack: () -> Unit
) {
    // 데모용 사용자 정보(추후 실제 계정 모델로 대체)
    val userName = "김철수"
    val email = "chulsoo@example.com"
    val joined = LocalDate(2025, 10, 1)

    val items = store.items
    val total = items.size
    val done = items.count { it.done }
    val thisWeek = items.count { it.due?.let { d -> isInThisWeek(d) } == true }
    val todayAdd = 0 // 샘플
    val rate = if (total == 0) 0 else (done * 100 / total)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("프로필") },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // 상단 프로필 영역
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 이니셜 배지
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.first().toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    AssistChip(
                        onClick = { /* 나중에 계정 설정으로 이동 */ },
                        label = { Text("계정 설정") }
                    )
                }
            }

            // 요약 통계
            ProfileSummaryRow(
                total = total,
                done = done,
                rate = rate
            )

            // 활동 요약 카드
            ActivitySummaryCard(
                thisWeek = thisWeek,
                todayAdd = todayAdd
            )

            // 가입 정보
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("계정 정보", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "가입일: ${joined.year}년 ${joined.monthNumber}월 ${joined.dayOfMonth}일",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "서비스 버전: 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileSummaryRow(
    total: Int,
    done: Int,
    rate: Int
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileStatCard(
            title = "총 할 일",
            value = "${total}개",
            highlight = false
        )
        ProfileStatCard(
            title = "완료",
            value = "${done}개",
            highlight = true
        )
        ProfileStatCard(
            title = "완료율",
            value = "$rate%",
            highlight = false
        )
    }
}

@Composable
private fun ProfileStatCard(
    title: String,
    value: String,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (highlight)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ActivitySummaryCard(
    thisWeek: Int,
    todayAdd: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("최근 활동", style = MaterialTheme.typography.titleMedium)
            Text(
                "이번 주에 완료한 할 일: ${thisWeek}개",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "오늘 추가한 할 일: ${todayAdd}개",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Clock.System 사용하지 않는 단순 버전
private fun isInThisWeek(date: LocalDate): Boolean {
    return true
}

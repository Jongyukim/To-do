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
    user: User?,
    onBack: () -> Unit
) {
    val userName = user?.name ?: "김철수"
    val email = user?.email ?: "chulsoo@example.com"
    val joined = LocalDate(2025, 10, 1)

    val items = store.items
    val total = items.size
    val done = items.count { it.done }
    val thisWeek = items.count { it.due?.let { d -> isInThisWeek(d) } == true }
    val todayAdd = 0 // 데모용
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

            // 상단 프로필 카드
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
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp),
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
                        onClick = { /* TODO: 계정 설정 */ },
                        label = { Text("계정 설정") }
                    )
                }
            }

            ProfileSummaryRow(
                total = total,
                done = done,
                rate = rate
            )

            ActivitySummaryCard(
                thisWeek = thisWeek,
                todayAdd = todayAdd
            )

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
            highlight = false,
            modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
            title = "완료",
            value = "${done}개",
            highlight = true,
            modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
            title = "완료율",
            value = "$rate%",
            highlight = false,
            modifier = Modifier.weight(1f)
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

// Clock.System 사용하지 않는 단순 버전 (데모)
private fun isInThisWeek(date: LocalDate): Boolean {
    return true
}

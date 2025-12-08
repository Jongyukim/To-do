@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smarttodo.data.FirebaseRepository
import com.example.smarttodo.data.FirestoreTodo
import kotlinx.datetime.*

@Composable
fun ProfileScreen(
    repository: FirebaseRepository, // [수정] store 대신 repository 사용
    onBack: () -> Unit,
    authManager: AuthManager
) {
    val userName = authManager.getCurrentUserDisplayName() ?: "User"
    val email = authManager.getCurrentUserEmail() ?: "N/A"

    // [추가] Firebase 데이터를 담을 변수
    var items by remember { mutableStateOf<List<Todo>>(emptyList()) }

    // [추가] 화면 켜질 때 데이터 불러오기
    LaunchedEffect(Unit) {
        try {
            items = repository.getAllTodos().map { it.toTodo() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val total = items.size
    val done = items.count { it.done }
    val thisWeek = items.count { it.due?.let { d -> isInThisWeek(d) } == true }
    val todayAdd = 0 // 샘플 (작성일 데이터가 없어서 0으로 둠)
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
                        Box(Modifier.size(88.dp), contentAlignment = Alignment.Center) {
                            Text(
                                userName.firstOrNull()?.toString() ?: "U",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                    }
                    Text(userName, fontWeight = FontWeight.SemiBold)
                    Text(email, color = Color.Gray)
                }
            }

            // 활동 통계 4칸
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                StatGrid(total = total, done = done, todayAdd = todayAdd, week = thisWeek)
            }

            // 완료율
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("완료율", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { if (total == 0) 0f else done.toFloat() / total },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("$done of $total", color = Color.Gray)
                }
            }

            // 주요 카테고리
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("주요 카테고리", style = MaterialTheme.typography.titleMedium)
                    // entries 권장
                    for (c in TodoCategory.entries) {
                        val catAll = items.count { it.category == c }
                        val catDone = items.count { it.category == c && it.done }
                        val p = if (catAll == 0) 0f else catDone.toFloat() / catAll

                        // 데이터가 있는 카테고리만 보여주거나, 없어도 보여주려면 유지
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(c.name, modifier = Modifier.width(56.dp))
                            LinearProgressIndicator(
                                progress = { p },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp),
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("$catDone/$catAll", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // 업적(샘플)
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("업적", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.weight(1f))
                        AssistChip(onClick = {}, label = { Text("2/4") })
                    }
                    AchievementRow("첫 할 일 완료", achieved = done > 0)
                    AchievementRow("연속 3일", achieved = false)
                    AchievementRow("10개 완료", achieved = done >= 10)
                    AchievementRow("완벽주의자(80%)", achieved = rate >= 80)
                }
            }

            // 연속 기록(샘플)
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("연속 기록", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "1일",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text("연속으로 할 일을 완료한 최고 기록", color = Color.Gray)
                }
            }

            // 수정 버튼
            Button(
                onClick = { /* TODO: 프로필 편집 */ },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) { Text("프로필 수정") }

            Spacer(Modifier.height(12.dp))
        }
    }
}

/* ---------- 컴포넌트 ---------- */

@Composable
private fun StatGrid(total: Int, done: Int, todayAdd: Int, week: Int) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallStatCard("전체 할 일", total, modifier = Modifier.weight(1f))
            SmallStatCard("완료된 일", done, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallStatCard("오늘 추가", todayAdd, modifier = Modifier.weight(1f))
            SmallStatCard("이번 주", week, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SmallStatCard(title: String, value: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                "$value",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AchievementRow(title: String, achieved: Boolean) {
    val bg = if (achieved) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }
    Surface(
        shape = MaterialTheme.shapes.large,
        color = bg,
        shadowElevation = if (achieved) 1.dp else 0.dp
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        if (achieved) "달성" else "미달성",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (achieved) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    labelColor = if (achieved) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            )
        }
    }
}

/* ---------- 날짜 유틸 & 매퍼 ---------- */

private fun isInThisWeek(date: LocalDate): Boolean {
    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(tz).date
    val start = today.minus(DatePeriod(days = today.dayOfWeek.isoDayNumber - 1)) // 월요일
    val end = start.plus(DatePeriod(days = 6))
    return date >= start && date <= end
}

// [추가] 데이터 변환 매퍼 (필수)
private fun FirestoreTodo.toTodo(): Todo {
    val category = try {
        TodoCategory.valueOf(this.category)
    } catch (e: IllegalArgumentException) {
        TodoCategory.개인
    }
    return Todo(
        id = this.id,
        title = this.title,
        category = category,
        due = this.due?.let { LocalDate.parse(it) },
        remind = this.remind,
        remindTime = this.remindTime,
        memo = this.memo,
        done = this.done
    )
}
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*

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
        ) {
            // 상단 프로필 영역
            Card(
                shape = MaterialTheme.shapes.extraLarge
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
                        tonalElevation = 2.dp
                    ) {
                        Box(Modifier.size(88.dp), contentAlignment = Alignment.Center) {
                            Text(
                                userName.first().toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                    }
                    Text(userName, fontWeight = FontWeight.SemiBold)
                    Text(email, color = Color.Gray)
                    AssistChip(onClick = {}, label = { Text("가입일: ${joined.year}년 ${joined.monthNumber}월") })
                }
            }

            // 활동 통계 4칸
            Card(Modifier.padding(top = 12.dp), shape = MaterialTheme.shapes.extraLarge) {
                StatGrid(total = total, done = done, todayAdd = todayAdd, week = thisWeek)
            }

            // 완료율
            Card(Modifier.padding(top = 12.dp), shape = MaterialTheme.shapes.extraLarge) {
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
            Card(Modifier.padding(top = 12.dp), shape = MaterialTheme.shapes.extraLarge) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("주요 카테고리", style = MaterialTheme.typography.titleMedium)
                    // values() 경고는 무시 가능. 바꾸려면 entries 사용.
                    for (c in TodoCategory.values()) {
                        val catAll = items.count { it.category == c }
                        val catDone = items.count { it.category == c && it.done }
                        val p = if (catAll == 0) 0f else catDone.toFloat() / catAll
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
            Card(Modifier.padding(top = 12.dp), shape = MaterialTheme.shapes.extraLarge) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("업적", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.weight(1f))
                        AssistChip(onClick = {}, label = { Text("2/4") })
                    }
                    AchievementRow("첫 할 일 완료", achieved = total > 0)
                    AchievementRow("연속 3일", achieved = false)
                    AchievementRow("10개 완료", achieved = done >= 10)
                    AchievementRow("완벽주의자(80%)", achieved = rate >= 80)
                }
            }

            // 연속 기록(샘플)
            Card(Modifier.padding(16.dp), shape = MaterialTheme.shapes.extraLarge) {
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
            SmallStatCard("전체 할 일", total, modifier = Modifier.weight(1f))   // ✅ weight는 부모 Row에서
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
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, color = Color.Gray)
            Text("$value", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AchievementRow(title: String, achieved: Boolean) {
    val bg = if (achieved) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    Surface(shape = MaterialTheme.shapes.large, color = bg) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(title, modifier = Modifier.weight(1f))
            Text(
                if (achieved) "달성" else "미달성",
                color = if (achieved) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
    }
}

/* ---------- 날짜 유틸 ---------- */

private fun isInThisWeek(date: LocalDate): Boolean {
    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(tz).date
    val start = today.minus(DatePeriod(days = today.dayOfWeek.isoDayNumber - 1)) // 월요일
    val end = start.plus(DatePeriod(days = 6))
    return date >= start && date <= end
}
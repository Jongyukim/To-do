@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smarttodo.data.FirebaseRepository
import com.example.smarttodo.data.FirestoreTodo
import kotlinx.datetime.LocalDate

@Composable
fun CategoryScreen(
    repository: FirebaseRepository, // [수정] store 대신 repository 사용
    onBack: () -> Unit
) {
    // [추가] Firebase 데이터를 담을 변수
    var allTodos by remember { mutableStateOf<List<Todo>>(emptyList()) }

    // [추가] 화면 켜질 때 데이터 불러오기
    LaunchedEffect(Unit) {
        try {
            allTodos = repository.getAllTodos().map { it.toTodo() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // [수정] 불러온 allTodos를 기준으로 통계 계산
    val stats = remember(allTodos) { computeCategoryStats(allTodos) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("카테고리") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()), // 스크롤 가능하게 변경
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 헤더 카드
            HeaderCard()

            // 카테고리 카드 리스트
            stats.forEach { s ->
                CategoryCard(stat = s)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

/* --------- 데이터/집계 --------- */

private data class CategoryStat(
    val category: TodoCategory,
    val done: Int,
    val total: Int
) {
    val active: Int get() = total - done
    val rate: Int get() = if (total == 0) 0 else (done * 100 / total)
}

// [수정] 매개변수를 TodoStore -> List<Todo>로 변경
private fun computeCategoryStats(items: List<Todo>): List<CategoryStat> {
    return TodoCategory.entries.map { cat -> // .values() 대신 .entries 사용 (권장)
        val all = items.filter { it.category == cat }
        val done = all.count { it.done }
        CategoryStat(category = cat, done = done, total = all.size)
    }
}

/* --------- UI 조각 --------- */

@Composable
private fun HeaderCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ElevatedAssistChip(
                onClick = {},
                label = { Text("카테고리별 관리", fontWeight = FontWeight.SemiBold) },
                colors = AssistChipDefaults.elevatedAssistChipColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            Text(
                "할 일을 카테고리별로 정리하고 관리하세요",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoryCard(stat: CategoryStat) {
    val (icon, name) = when (stat.category) {
        TodoCategory.학업 -> Icons.Filled.School to "학업"
        TodoCategory.개인 -> Icons.Filled.Person to "개인"
        TodoCategory.업무 -> Icons.Filled.Work to "업무"
        TodoCategory.기타 -> Icons.Filled.PushPin to "기타"
    }

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.large,
                    shadowElevation = 1.dp
                ) {
                    Box(Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${stat.done}/${stat.total} 완료",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { if (stat.total == 0) 0f else stat.done.toFloat() / stat.total },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text("진행 중 ${stat.active}", fontWeight = FontWeight.Medium) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                AssistChip(
                    onClick = {},
                    label = { Text("${stat.rate}% 완료", fontWeight = FontWeight.Medium) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

// ---------------------------------------------------------
// [추가] 데이터 매퍼
// ---------------------------------------------------------
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
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smarttodo.data.FirebaseRepository
import com.example.smarttodo.data.FirestoreTodo
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@Composable
fun NotificationScreen(
    store: TodoStore,
    repository: FirebaseRepository, // [수정] repository 추가
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
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

    // [수정] 불러온 데이터에서 알림이 켜진 것만 필터링
    val scheduled = allTodos.filter { it.remind }
    val total = allTodos.size
    val scheduledCount = scheduled.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("알림") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "뒤로") }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // 요약 카드
            SummaryCard(scheduledCount = scheduledCount, total = total)

            // 섹션 타이틀
            Row(
                Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text("예정된 알림", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                AssistChip(onClick = {}, label = { Text("$scheduledCount") })
            }

            if (scheduled.isEmpty()) {
                EmptyNotifications()
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    scheduled.forEach { todo ->
                        NotificationRow(
                            todo = todo,
                            onToggle = {
                                // [수정] 스위치 토글 시 Firebase 업데이트
                                scope.launch {
                                    val updatedTodo = todo.copy(remind = !todo.remind)
                                    repository.updateTodo(updatedTodo.toFirestoreTodo())
                                    // 목록 새로고침
                                    allTodos = repository.getAllTodos().map { it.toTodo() }
                                }
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SummaryCard(scheduledCount: Int, total: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ElevatedAssistChip(
                    onClick = {},
                    label = { Text("알림 요약", fontWeight = FontWeight.SemiBold) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Alarm,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = AssistChipDefaults.elevatedAssistChipColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        labelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "설정된 알림을 확인하세요",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                Column {
                    Text(
                        "예정된 알림",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "$scheduledCount",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        "전체 알림",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "$total",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    todo: Todo,
    onToggle: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘 배지
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.large,
                shadowElevation = 1.dp
            ) {
                Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Alarm,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text(todo.category.name, fontWeight = FontWeight.Medium) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )

                    // 알림 시간 표시
                    if (todo.remindTime != null) {
                        AssistChip(
                            onClick = {},
                            label = { Text(todo.remindTime, fontWeight = FontWeight.Medium) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    } else {
                        // 시간이 없으면 날짜 표시
                        AssistChip(
                            onClick = {},
                            label = { Text(todo.due?.toString() ?: "미정", fontWeight = FontWeight.Medium) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))
            Switch(
                checked = todo.remind,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
private fun EmptyNotifications() {
    Surface(
        Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        shadowElevation = 0.dp,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("예정된 알림이 없습니다", color = Color.Gray)
        }
    }
}

// ---------------------------------------------------------
// [추가] 데이터 매퍼 (오류 방지용)
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

private fun Todo.toFirestoreTodo(): FirestoreTodo {
    return FirestoreTodo(
        id = this.id,
        title = this.title,
        category = this.category.name,
        due = this.due?.toString(),
        remind = this.remind,
        remindTime = this.remindTime,
        memo = this.memo,
        done = this.done
    )
}
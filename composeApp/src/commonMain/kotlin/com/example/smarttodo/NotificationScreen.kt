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
import kotlinx.datetime.*

@Composable
fun NotificationScreen(
    store: TodoStore,
    repository: FirebaseRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var allTodos by remember { mutableStateOf<List<Todo>>(emptyList()) }

    // 현재 시간 정보 가져오기
    val now = Clock.System.now()
    val tz = TimeZone.currentSystemDefault()
    val today = now.toLocalDateTime(tz).date
    val currentTime = now.toLocalDateTime(tz).time

    LaunchedEffect(Unit) {
        try {
            allTodos = repository.getAllTodos().map { it.toTodo() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // [수정 핵심] 필터링 후 -> 날짜 순서대로 정렬 추가 (.sortedWith)
    val scheduled = allTodos.filter { todo ->
        // 1. 알림 기능이 켜져 있거나, 시간이 설정된 것만 대상
        val isNotifiable = todo.remind || !todo.remindTime.isNullOrBlank()
        if (!isNotifiable) return@filter false

        // 2. 날짜 비교 (과거인지 확인)
        val dueDate = todo.due

        if (dueDate == null) {
            true // 날짜가 없으면 일단 보여줌
        } else if (dueDate < today) {
            false // 1. 날짜가 어제보다 전이면 -> 숨김
        } else if (dueDate == today) {
            // 2. 오늘 날짜라면 -> 시간이 지났는지 확인
            val timeStr = todo.remindTime
            if (timeStr.isNullOrBlank()) {
                true // 시간 설정 없으면 오늘 하루 종일 표시
            } else {
                try {
                    val dueTime = LocalTime.parse(timeStr)
                    dueTime > currentTime // 현재 시간보다 미래인 경우만 표시
                } catch (e: Exception) {
                    true // 시간 형식 에러나면 그냥 표시
                }
            }
        } else {
            true // 3. 미래 날짜면 -> 표시
        }
    }.sortedWith(
        compareBy<Todo> { it.due }        // 1순위: 날짜 빠른 순
            .thenBy { it.remindTime }     // 2순위: 시간 빠른 순
    )

    val total = allTodos.size
    val activeCount = scheduled.count { it.remind }

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
            SummaryCard(activeCount = activeCount, total = total)

            // 섹션 타이틀
            Row(
                Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text("예정된 알림", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                AssistChip(onClick = {}, label = { Text("${scheduled.size}") })
            }

            if (scheduled.isEmpty()) {
                EmptyNotifications()
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    scheduled.forEach { todo ->
                        NotificationRow(
                            todo = todo,
                            onToggle = {
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
private fun SummaryCard(activeCount: Int, total: Int) {
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
                "다가오는 알림을 확인하세요",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                Column {
                    Text(
                        "활성 알림",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "$activeCount",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        "전체 할 일",
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
    val containerColor = if (todo.remind) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (todo.remind) 2.dp else 0.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (todo.remind) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.large,
                shadowElevation = 0.dp
            ) {
                Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Alarm,
                        contentDescription = null,
                        tint = if (todo.remind) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (todo.remind) MaterialTheme.colorScheme.onSurface else Color.Gray
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
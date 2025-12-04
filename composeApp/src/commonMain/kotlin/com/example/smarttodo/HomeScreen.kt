// HomeScreen.kt
@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.smarttodo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smarttodo.data.FirebaseRepository
import com.example.smarttodo.data.FirestoreTodo
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import java.util.UUID

// Extension function for String to LocalDate conversion
private fun String.toLocalDate(): LocalDate {
    return LocalDate.parse(this)
}

// Mapper functions to convert between UI model and Firestore model
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

private fun FirestoreTodo.toTodo(): Todo {
    // Handle potential errors if a string value from Firestore doesn't match the enum
    val category = try {
        TodoCategory.valueOf(this.category)
    } catch (e: IllegalArgumentException) {
        TodoCategory.개인 // Default to 'Personal' category on error
    }
    return Todo(
        id = this.id,
        title = this.title,
        category = category,
        due = this.due?.toLocalDate(),
        remind = this.remind,
        remindTime = this.remindTime,
        memo = this.memo,
        done = this.done
    )
}


@Composable
fun HomeScreen(
    repository: FirebaseRepository,
    onOpenCategory: () -> Unit = {},
    onOpenCalendar: () -> Unit = {},
    onOpenAlarm: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenStats: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(Filter.All) }
    var showEditor by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Todo?>(null) }

    // State for holding todos fetched from Firebase
    var allTodos by remember { mutableStateOf<List<Todo>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Function to refresh the todo list from Firestore
    val refreshTodos = {
        coroutineScope.launch {
            allTodos = repository.getAllTodos().map { it.toTodo() }
        }
    }

    // Fetch initial data
    LaunchedEffect(Unit) {
        refreshTodos()
    }

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val filtered = allTodos.filter {
        val matchQuery = query.isBlank() || it.title.contains(query, true)
        val matchFilter = when (filter) {
            Filter.All -> true
            // "오늘" 탭: 아직 완료되지 않은 할 일
            Filter.Today -> !todo.done
            // "다가오는" 탭: 마감일이 있고 미완료인 할 일
            Filter.Upcoming -> todo.due != null && !todo.done
            // "완료됨" 탭
            Filter.Done -> todo.done
        }
        matchQuery && matchFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("스마트 To-Do", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onOpenProfile) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "프로필"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editing = null
                    showEditor = true
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "할 일 추가")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "홈") },
                    label = { Text("홈") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenCalendar,
                    icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "캘린더") },
                    label = { Text("캘린더") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenStats,
                    icon = { Icon(Icons.Filled.Assessment, contentDescription = "통계") },
                    label = { Text("통계") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenSettings,
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "설정") },
                    label = { Text("설정") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // KPI
            item {
                KPISection(
                    totalToday = allTodos.count { it.due == today },
                    activeCount = allTodos.count { !it.done },
                    doneRate = allTodos.let { if (it.isEmpty()) 0 else (it.count { t -> t.done } * 100 / it.size) }
                )
            }

            // 퀵 액션
            item {
                QuickActions(
                    onCategory = onOpenCategory, onCalendar = onOpenCalendar,
                    onAlarm = onOpenAlarm, onProfile = onOpenProfile
                )
            }

            // 검색
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("할 일 검색") }
            )

            // 필터 칩
            FilterChipsRow(
                selected = filter,
                onSelect = { filter = it }
            )

            Spacer(Modifier.height(4.dp))

            if (filtered.isEmpty()) {
                EmptyState(filter = filter)
            } else {
                items(filtered, key = { it.id }) { todo ->
                    TodoRow(
                        todo = todo,
                        onToggle = {
                            coroutineScope.launch {
                                val updatedTodo = todo.copy(done = !todo.done)
                                repository.updateTodo(updatedTodo.toFirestoreTodo())
                                refreshTodos()
                            }
                        },
                        onEdit = { editing = todo; showEditor = true },
                        onDelete = {
                            coroutineScope.launch {
                                repository.deleteTodo(todo.id)
                                refreshTodos()
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (showEditor) {
        EditTodoSheet(
            initial = editing,
            onDismiss = { showEditor = false },
            onSubmit = { t ->
                coroutineScope.launch {
                     if (editing == null) {
                        // For new items, ensure ID is set
                        val newTodo = if(t.id.isBlank()) t.copy(id = UUID.randomUUID().toString()) else t
                        repository.addTodo(newTodo.toFirestoreTodo())
                    } else {
                        // For existing items, just update
                        repository.updateTodo(t.toFirestoreTodo())
                    }
                    showEditor = false
                    refreshTodos()
                }
            }
        )
    }
}

/* ───────── 리스트 아이템 ───────── */

@Composable
private fun TodoRow(
    todo: Todo,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menu by remember { mutableStateOf(false) }

    val container = if (todo.done) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    else MaterialTheme.colorScheme.surface
    val checkColor = if (todo.done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

        if (showEditor) {
            EditTodoSheet(
                initial = editing,
                onDismiss = {
                    showEditor = false
                    editing = null
                },
                onSubmit = { submitted ->
                    if (editing == null) {
                        store.add(submitted)
                    } else {
                        // 같은 id 유지하면서 내용만 교체
                        store.update(submitted.id) { submitted }
                    }
                    showEditor = false
                    editing = null
                }
            )
        }
    }
}

private enum class Filter { All, Today, Upcoming, Done }

@Composable
private fun FilterChipsRow(
    selected: Filter,
    onSelect: (Filter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Filter.values().forEach { f ->
            AssistChip(
                onClick = { onSelect(f) },
                label = {
                    Text(
                        text = when (f) {
                            Filter.All -> "전체"
                            Filter.Today -> "오늘"
                            Filter.Upcoming -> "다가오는"
                            Filter.Done -> "완료됨"
                        }
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (f == selected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (f == selected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun TodoItemRow(
    todo: Todo,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = todo.done,
                    onCheckedChange = { onToggle() }
                )
                Spacer(Modifier.width(8.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    val dueText = todo.due?.toString() ?: "마감일 없음"
                    Text(
                        text = "카테고리: ${todo.category} · 마감: $dueText",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onEdit) {
                    Text("수정")
                }
                TextButton(onClick = onDelete) {
                    Text("삭제")
                }
            }
        }
    }
}

@Composable
private fun EmptyState(filter: Filter) {
    val text = when (filter) {
        Filter.All -> "등록된 할 일이 없습니다.\n오른쪽 아래 + 버튼으로 새 할 일을 추가하세요."
        Filter.Today -> "오늘 할 일이 없습니다."
        Filter.Upcoming -> "다가오는 할 일이 없습니다."
        Filter.Done -> "완료된 할 일이 아직 없습니다."
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

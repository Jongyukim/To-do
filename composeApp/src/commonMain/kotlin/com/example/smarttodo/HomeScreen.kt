@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)

package com.example.smarttodo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.smarttodo.data.FirebaseRepository
import com.example.smarttodo.data.FirestoreTodo
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import java.util.UUID

// Extension function & Mappers
private fun String.toLocalDate(): LocalDate {
    return LocalDate.parse(this)
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
    var showEditor by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Todo?>(null) }

    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(Filter.All) }

    // [추가] 카테고리 필터 상태 (null이면 전체 보기)
    var selectedCategory by remember { mutableStateOf<TodoCategory?>(null) }

    var allTodos by remember { mutableStateOf<List<Todo>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    val refreshTodos = {
        coroutineScope.launch {
            try {
                allTodos = repository.getAllTodos()
                    .map { it.toTodo() }
                    .sortedWith(compareBy<Todo, LocalDate?>(nullsLast()) { it.due })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshTodos()
    }

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    // [수정] 필터링 로직에 카테고리 조건 추가
    val filtered = allTodos.filter { todo ->
        val matchQuery = query.isBlank() || todo.title.contains(query, true)

        // 상단 탭 필터 (전체/오늘/다가오는/완료됨)
        val matchFilter = when (filter) {
            Filter.All -> true
            Filter.Today -> todo.due == today
            Filter.Upcoming -> todo.due?.let { d -> d > today } ?: false
            Filter.Done -> todo.done
        }

        // [추가] 카테고리 필터 (선택 안함 OR 선택된 카테고리와 일치)
        val matchCategory = selectedCategory == null || todo.category == selectedCategory

        matchQuery && matchFilter && matchCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("스마트 To-Do", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editing = null; showEditor = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "추가")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Home, null) },
                    label = { Text("홈") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenStats,
                    icon = { Icon(Icons.Filled.Assessment, null) },
                    label = { Text("통계") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenSettings,
                    icon = { Icon(Icons.Filled.Settings, null) },
                    label = { Text("설정") }
                )
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(pad),
            contentPadding = PaddingValues(bottom = 96.dp)
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

            // [추가] 카테고리 필터 섹션 (네모난 박스들)
            item {
                CategoryFilterSection(
                    selected = selectedCategory,
                    onSelect = {
                        // 이미 선택된 걸 또 누르면 해제(전체보기), 아니면 선택
                        selectedCategory = if (selectedCategory == it) null else it
                    }
                )
            }

            // 검색
            item {
                OutlinedTextField(
                    value = query, onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    placeholder = { Text("할 일 검색…") }, singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.Search, null) }
                )
                Spacer(Modifier.height(10.dp))
            }

            // 필터 칩 (전체/오늘/다가오는/완료됨)
            item {
                FilterChips(selected = filter, onSelect = { filter = it }, modifier = Modifier.padding(horizontal = 12.dp))
                Spacer(Modifier.height(8.dp))
            }

            if (filtered.isEmpty()) {
                item { EmptyState() }
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
                        val newTodo = if(t.id.isBlank()) t.copy(id = UUID.randomUUID().toString()) else t
                        repository.addTodo(newTodo.toFirestoreTodo())
                    } else {
                        repository.updateTodo(t.toFirestoreTodo())
                    }
                    showEditor = false
                    refreshTodos()
                }
            }
        )
    }
}

// ──────────────────────────────────────────────
// Sub Components
// ──────────────────────────────────────────────

// [추가] 카테고리 필터 섹션 UI
@Composable
private fun CategoryFilterSection(
    selected: TodoCategory?,
    onSelect: (TodoCategory) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "카테고리별 보기",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 모든 카테고리를 순회하며 네모난 카드 생성
            TodoCategory.entries.forEach { cat ->
                val isSelected = (selected == cat)
                val color = when (cat) {
                    TodoCategory.학업 -> Color(0xFF6366F1)
                    TodoCategory.업무 -> Color(0xFF10B981)
                    TodoCategory.개인 -> Color(0xFFF59E0B)
                    TodoCategory.기타 -> Color(0xFF8B5CF6)
                }
                val icon = when (cat) {
                    TodoCategory.학업 -> Icons.Filled.School
                    TodoCategory.업무 -> Icons.Filled.Work
                    TodoCategory.개인 -> Icons.Filled.Person
                    TodoCategory.기타 -> Icons.Filled.PushPin
                }

                CategoryFilterCard(
                    title = cat.name,
                    icon = icon,
                    color = color,
                    isSelected = isSelected,
                    onClick = { onSelect(cat) },
                    modifier = Modifier.weight(1f) // 균등하게 꽉 채우기
                )
            }
        }
    }
}

// [추가] 네모난 카테고리 필터 카드
@Composable
private fun CategoryFilterCard(
    title: String,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp), // 높이 고정
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color else MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

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

    Surface(
        color = container,
        shadowElevation = if (todo.done) 0.dp else 1.dp
    ) {
        ListItem(
            leadingContent = {
                Checkbox(
                    checked = todo.done,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(checkedColor = checkColor)
                )
            },
            headlineContent = {
                Text(
                    todo.title,
                    fontWeight = if (todo.done) FontWeight.Normal else FontWeight.SemiBold,
                    textDecoration = if (todo.done) TextDecoration.LineThrough else TextDecoration.None
                )
            },
            supportingContent = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text(todo.category.name) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (todo.done) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                            else MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = if (todo.done) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    val dueText = todo.due?.toString() ?: "마감 없음"
                    AssistChip(
                        onClick = {},
                        label = { Text(dueText) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (todo.done) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    if (todo.done) {
                        AssistChip(
                            onClick = {},
                            label = { Text("완료") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                labelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            },
            trailingContent = {
                Box {
                    IconButton(onClick = { menu = true }) { Icon(Icons.Filled.MoreVert, contentDescription = "메뉴") }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        DropdownMenuItem(text = { Text("수정") }, onClick = { menu = false; onEdit() })
                        DropdownMenuItem(
                            text = { Text("삭제", color = MaterialTheme.colorScheme.error) },
                            onClick = { menu = false; onDelete() }
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun KPISection(totalToday: Int, activeCount: Int, doneRate: Int) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        KPIBadge(
            title = "오늘",
            value = totalToday.toString(),
            color = Color(0xFF6366F1),
            icon = Icons.Filled.CalendarMonth,
            modifier = Modifier.weight(1f)
        )
        KPIBadge(
            title = "진행",
            value = activeCount.toString(),
            color = Color(0xFF10B981),
            icon = Icons.Filled.Assessment,
            modifier = Modifier.weight(1f)
        )
        KPIBadge(
            title = "완료율",
            value = "$doneRate%",
            color = Color(0xFFF59E0B),
            icon = Icons.Filled.Assessment,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun KPIBadge(
    title: String,
    value: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
                }
            }
            Text(value, style = MaterialTheme.typography.headlineLarge, color = color, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelMedium, color = color.copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun QuickActions(
    onCategory: () -> Unit,
    onCalendar: () -> Unit,
    onAlarm: () -> Unit,
    onProfile: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionIcon("카테고리", Icons.Filled.Category, onCategory, Color(0xFF6366F1))
            ActionIcon("캘린더", Icons.Filled.CalendarMonth, onCalendar, Color(0xFF10B981))
            ActionIcon("알림", Icons.Filled.Notifications, onAlarm, Color(0xFFF59E0B))
            ActionIcon("프로필", Icons.Filled.Settings, onProfile, Color(0xFF8B5CF6))
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun ActionIcon(label: String, icon: ImageVector, onClick: () -> Unit, iconColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
        Surface(
            onClick = onClick,
            shape = MaterialTheme.shapes.large,
            color = iconColor.copy(alpha = 0.1f),
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
    }
}

private enum class Filter { All, Today, Upcoming, Done }

@Composable
private fun FilterChips(
    selected: Filter,
    onSelect: (Filter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SuggestionChip({ onSelect(Filter.All) }, { Text("전체") }, colors = chipColors(selected == Filter.All))
        SuggestionChip({ onSelect(Filter.Today) }, { Text("오늘") }, colors = chipColors(selected == Filter.Today))
        SuggestionChip({ onSelect(Filter.Upcoming) }, { Text("다가오는") }, colors = chipColors(selected == Filter.Upcoming))
        SuggestionChip({ onSelect(Filter.Done) }, { Text("완료됨") }, colors = chipColors(selected == Filter.Done))
    }
}

@Composable
private fun chipColors(sel: Boolean) =
    SuggestionChipDefaults.suggestionChipColors(
        containerColor = if (sel) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
        labelColor = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    )

@Composable
private fun EmptyState() {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(96.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            shadowElevation = 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Add, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("할 일이 없습니다", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(8.dp))
        Text("새로운 할 일을 추가해보세요", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}
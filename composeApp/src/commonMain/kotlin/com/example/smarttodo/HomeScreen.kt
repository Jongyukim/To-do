@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)

package com.example.smarttodo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import jdk.internal.vm.vector.VectorSupport.store
import kotlinx.datetime.*

@Composable
fun HomeScreen(
    store: TodoStore,
    onOpenCategory: () -> Unit = {},
    onOpenCalendar: () -> Unit = {},
    onOpenAlarm: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenStats: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
) {
    // 편집 시트 상태
    var showEditor by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Todo?>(null) }

    var tabIndex by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(Filter.All) }

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val filtered = store.items.filter {
        val matchQuery = query.isBlank() || it.title.contains(query, true)
        val matchFilter = when (filter) {
            Filter.All -> true
            Filter.Today -> it.due == today
            Filter.Upcoming -> it.due?.let { d -> d > today } ?: false
            Filter.Done -> it.done
        }
        matchQuery && matchFilter
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("스마트 To-Do") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showEditor = true }) {
                Icon(Icons.Filled.Add, contentDescription = "추가")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* stay */ },
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
                    totalToday = store.items.count { it.due == today },
                    activeCount = store.items.count { !it.done },
                    doneRate = store.items.let { if (it.isEmpty()) 0 else (it.count { t -> t.done } * 100 / it.size) }
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
            item {
                OutlinedTextField(
                    value = query, onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    placeholder = { Text("할 일 검색…") }, singleLine = true
                )
                Spacer(Modifier.height(10.dp))
            }

            // 필터 칩
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
                        onToggle = { store.toggleDone(todo.id) },
                        onEdit = { editing = todo; showEditor = true },
                        onDelete = { store.remove(todo.id) }
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
                if (editing == null) store.add(t) else store.update(t.id) { _ -> t }
                showEditor = false
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

    Surface(
        color = container,
        tonalElevation = if (todo.done) 0.dp else 1.dp
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
                    // 카테고리 칩
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
                    // 마감일 칩
                    val dueText = todo.due?.toString() ?: "마감 없음"
                    AssistChip(
                        onClick = {},
                        label = { Text(dueText) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (todo.done) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    // 완료 표시 칩 (완료일은 여기선 표시 생략)
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
/* ───────── KPI/Quick/Filter/Empty ───────── */

@Composable
private fun KPISection(totalToday: Int, activeCount: Int, doneRate: Int) {
    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        KPIBadge(
            title = "오늘",
            value = totalToday.toString(),
            color = Color(0xFF6C63FF),
            modifier = Modifier.weight(1f)
        )
        KPIBadge(
            title = "진행",
            value = activeCount.toString(),
            color = Color(0xFF26A69A),
            modifier = Modifier.weight(1f)
        )
        KPIBadge(
            title = "완료율",
            value = "$doneRate%",
            color = Color(0xFFF1A33B),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun KPIBadge(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(color.copy(alpha = 0.12f))
            .padding(14.dp)
    ) {
        Text(title, color = color, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Text(value, style = MaterialTheme.typography.headlineSmall, color = color)
    }
}

@Composable
private fun QuickActions(
    onCategory: () -> Unit,
    onCalendar: () -> Unit,
    onAlarm: () -> Unit,
    onProfile: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionIcon("카테고리", Icons.Filled.Category, onCategory)
        ActionIcon("캘린더", Icons.Filled.CalendarMonth, onCalendar)
        ActionIcon("알림", Icons.Filled.Assessment, onAlarm)
        ActionIcon("프로필", Icons.Filled.Settings, onProfile)
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun ActionIcon(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(onClick = onClick) { Icon(icon, contentDescription = label) }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
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
        Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(Modifier.size(88.dp), shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surfaceVariant) {}
        Spacer(Modifier.height(12.dp))
        Text("할 일이 없습니다", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text("새로운 할 일을 추가해보세요", color = Color.Gray, textAlign = TextAlign.Center)
    }
}

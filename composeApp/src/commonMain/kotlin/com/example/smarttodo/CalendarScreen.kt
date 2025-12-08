@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smarttodo.data.FirebaseRepository
import com.example.smarttodo.data.FirestoreTodo
import kotlinx.coroutines.launch
import kotlinx.datetime.*

@Composable
fun CalendarScreen(
    repository: FirebaseRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope() // [추가] 비동기 작업을 위한 스코프
    val tz = TimeZone.currentSystemDefault()
    val today = remember { Clock.System.now().toLocalDateTime(tz).date }

    var current by remember { mutableStateOf(today) }   // 현재 보이는 월
    var selected by remember { mutableStateOf(today) }  // 선택된 날짜

    // Firebase 데이터를 담을 변수
    var allTodos by remember { mutableStateOf<List<Todo>>(emptyList()) }

    // 데이터 불러오는 함수 (새로고침용)
    fun refreshData() {
        scope.launch {
            try {
                allTodos = repository.getAllTodos().map { it.toTodo() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 화면 켜질 때 데이터 불러오기
    LaunchedEffect(Unit) {
        refreshData()
    }

    val ym = YearMonthK(current.year, current.month)
    val monthCells = remember(ym) { buildMonthCells(ym) }

    val dayTodos = remember(selected, allTodos) {
        allTodos.filter { it.due == selected }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("캘린더") },
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
            // 월 헤더
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { current = current.minus(DatePeriod(months = 1)) }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "이전 달")
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${ym.year}년 ${ym.monthNumber}월",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { current = today; selected = today }) { Text("오늘") }
                IconButton(onClick = { current = current.plus(DatePeriod(months = 1)) }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "다음 달")
                }
            }

            // 요일 헤더
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("일","월","화","수","목","금","토").forEach {
                    Text(
                        it,
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(6.dp))

            // 달력 그리드
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                monthCells.chunked(7).forEach { week ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.forEach { day ->
                            DayCell(
                                day = day,
                                isToday = day == today,
                                isSelected = day == selected,
                                hasTodos = day != null && allTodos.any { it.due == day },
                                onClick = {
                                    day?.let {
                                        selected = it
                                        current = it
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 선택 날짜 섹션
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val wnames = listOf("일","월","화","수","목","금","토")
                val widx = selected.dayOfWeek.isoDayNumber % 7   // Sun=0
                Text(
                    "${selected.year}년 ${selected.monthNumber}월 ${selected.dayOfMonth}일 ${wnames[widx]}요일",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                AssistChip(onClick = {}, label = { Text("${dayTodos.size}개") })
            }

            Spacer(Modifier.height(8.dp))

            if (dayTodos.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("해당 날짜의 할 일이 없습니다", color = Color.Gray)
                }
            } else {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dayTodos.forEach { todo ->
                        // [수정] 체크박스 동작 연결
                        TodoCardCompact(
                            todo = todo,
                            onToggle = {
                                scope.launch {
                                    val updatedTodo = todo.copy(done = !todo.done)
                                    repository.updateTodo(updatedTodo.toFirestoreTodo())
                                    refreshData() // 목록 새로고침
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/* ---------- Sub UIs ---------- */

@Composable
private fun DayCell(
    day: LocalDate?,
    isToday: Boolean,
    isSelected: Boolean,
    hasTodos: Boolean,
    onClick: () -> Unit
) {
    val size = 48.dp
    Surface(
        modifier = Modifier
            .width(size)
            .height(size),
        shape = MaterialTheme.shapes.medium,
        color = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else -> Color.Transparent
        },
        onClick = { if (day != null) onClick() },
        enabled = day != null
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (day != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = day.dayOfMonth.toString(),
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            isToday -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (hasTodos) {
                        Spacer(Modifier.height(2.dp))
                        Box(
                            Modifier
                                .size(4.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.primary
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodoCardCompact(
    todo: Todo,
    onToggle: () -> Unit // [추가] 토글 콜백
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = if (todo.done) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        } else {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        },
        shadowElevation = 1.dp
    ) {
        ListItem(
            leadingContent = {
                Checkbox(
                    checked = todo.done,
                    onCheckedChange = { onToggle() }, // [수정] 클릭 시 onToggle 실행
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            headlineContent = {
                Text(
                    todo.title,
                    fontWeight = if (todo.done) FontWeight.Normal else FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            supportingContent = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text(todo.category.name, fontWeight = FontWeight.Medium) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(todo.due?.toString() ?: "마감 없음", fontWeight = FontWeight.Medium) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                }
            }
        )
    }
}

/* ---------- Date helpers & Mapper ---------- */

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

private data class YearMonthK(val year: Int, val month: Month) {
    val monthNumber: Int get() = month.number
}

private fun buildMonthCells(ym: YearMonthK): List<LocalDate?> {
    val first = LocalDate(ym.year, ym.month, 1)
    val days = daysInMonth(ym.year, ym.month)
    val sunBasedOffset = first.dayOfWeek.isoDayNumber % 7

    val cells = mutableListOf<LocalDate?>()
    repeat(sunBasedOffset) { cells.add(null) }
    repeat(days) { i -> cells.add(LocalDate(ym.year, ym.month, i + 1)) }
    while (cells.size % 7 != 0) cells.add(null)
    return cells
}

private fun daysInMonth(year: Int, month: Month): Int = when (month.number) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (isLeapYear(year)) 29 else 28
    else -> 30
}

private fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
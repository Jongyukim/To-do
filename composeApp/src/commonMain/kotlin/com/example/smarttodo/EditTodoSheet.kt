@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.smarttodo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@Composable
fun EditTodoSheet(
    initial: Todo? = null,
    onDismiss: () -> Unit,
    onSubmit: (Todo) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title.orEmpty()) }
    var category by remember { mutableStateOf(initial?.category ?: TodoCategory.개인) }
    var dueText by remember { mutableStateOf(initial?.due?.toString().orEmpty()) } // YYYY-MM-DD
    var remind by remember { mutableStateOf(initial?.remind ?: false) }
    var remindTime by remember { mutableStateOf(initial?.remindTime.orEmpty()) }   // HH:mm
    var memo by remember { mutableStateOf(initial?.memo.orEmpty()) }

    ModalBottomSheet(onDismissRequest = onDismiss, dragHandle = null) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // 헤더
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (initial == null) "새 할 일" else "할 일 수정", style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "닫기") }
            }

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("제목 *") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("할 일을 입력하세요") }
            )

            // 카테고리 드롭다운
            var catExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
                OutlinedTextField(
                    value = category.name, onValueChange = {}, readOnly = true,
                    label = { Text("카테고리") }, modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                    TodoCategory.values().forEach { c ->
                        DropdownMenuItem(text = { Text(c.name) }, onClick = { category = c; catExpanded = false })
                    }
                }
            }

            OutlinedTextField(
                value = dueText, onValueChange = { dueText = it },
                label = { Text("마감일 (YYYY-MM-DD)") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            // 알림 섹션 (UI만)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (remind) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("알림 설정")
                        Switch(checked = remind, onCheckedChange = { remind = it })
                    }
                    if (remind) {
                        OutlinedTextField(
                            value = remindTime, onValueChange = { remindTime = it },
                            label = { Text("알림 시간 (HH:mm)") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            OutlinedTextField(
                value = memo, onValueChange = { memo = it },
                label = { Text("메모") }, modifier = Modifier.fillMaxWidth(), minLines = 3
            )

            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("취소") }
                Spacer(Modifier.width(8.dp))
                val enable = title.isNotBlank()
                Button(
                    enabled = enable,
                    onClick = {
                        val due = dueText.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
                        val base = initial ?: Todo(
                            id = randomId(), title = title.trim(), category = category,
                            due = due, remind = remind, remindTime = remindTime.ifBlank { null },
                            memo = memo.trim(), done = false
                        )
                        val edited = if (initial != null) {
                            initial.copy(
                                title = title.trim(),
                                category = category,
                                due = due,
                                remind = remind,
                                remindTime = remindTime.ifBlank { null },
                                memo = memo.trim()
                            )
                        } else base
                        onSubmit(edited)
                    }
                ) { Text(if (initial == null) "추가" else "수정") }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun randomId(): String {
    val a = ('a'..'z').toList()
    return buildString {
        repeat(6) { append(a.random()) }
        append('-')
        append((1000..9999).random())
    }
}
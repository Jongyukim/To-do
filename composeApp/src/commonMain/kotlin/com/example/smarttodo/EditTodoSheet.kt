@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.smarttodo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
                    containerColor = if (remind) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = if (remind) 1.dp else 0.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "알림 설정",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = remind,
                            onCheckedChange = { remind = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                    if (remind) {
                        var timeError by remember { mutableStateOf<String?>(null) }
                        OutlinedTextField(
                            value = remindTime,
                            onValueChange = { 
                                remindTime = it
                                // 시간 형식 검증 (HH:mm)
                                timeError = if (it.isNotBlank() && !it.matches(Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$"))) {
                                    "올바른 시간 형식이 아닙니다 (예: 09:30)"
                                } else null
                            },
                            label = { Text("알림 시간 (HH:mm)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("예: 09:30") },
                            isError = timeError != null,
                            supportingText = timeError?.let { { Text(it) } }
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
                val timeValid = !remind || remindTime.isBlank() || remindTime.matches(Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$"))
                val enable = title.isNotBlank() && timeValid
                Button(
                    enabled = enable,
                    onClick = {
                        val due = dueText.takeIf { it.isNotBlank() }?.let { 
                            try {
                                LocalDate.parse(it)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        // 알림이 켜져있으면 기본 시간 설정 (09:00)
                        val finalRemindTime = if (remind && remindTime.isBlank()) "09:00" else remindTime.ifBlank { null }
                        val base = initial ?: Todo(
                            id = randomId(), title = title.trim(), category = category,
                            due = due, remind = remind, remindTime = finalRemindTime,
                            memo = memo.trim(), done = false
                        )
                        val edited = if (initial != null) {
                            initial.copy(
                                title = title.trim(),
                                category = category,
                                due = due,
                                remind = remind,
                                remindTime = finalRemindTime,
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
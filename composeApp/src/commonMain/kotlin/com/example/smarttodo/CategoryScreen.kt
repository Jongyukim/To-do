@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CategoryScreen(
    store: TodoStore,
    onBack: () -> Unit
) {
    val stats = remember(store.items) { computeCategoryStats(store) }

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
                .padding(horizontal = 16.dp),
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

private fun computeCategoryStats(store: TodoStore): List<CategoryStat> {
    val items = store.items
    return TodoCategory.values().map { cat ->
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
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ElevatedAssistChip(
                onClick = {},
                label = { Text("카테고리별 관리") }
            )
            Text("할 일을 카테고리별로 정리하고 관리하세요", color = Color.Gray)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ElevatedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text("${stat.done}/${stat.total}")
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { if (stat.total == 0) 0f else stat.done.toFloat() / stat.total },
                modifier = Modifier.fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = {}, label = { Text("진행 중 ${stat.active}") })
                Spacer(Modifier.width(8.dp))
                AssistChip(onClick = {}, label = { Text("${stat.rate}% 완료") })
            }
        }
    }
}
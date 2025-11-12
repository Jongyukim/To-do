@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    store: TodoStore,
    onBackHome: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val items = store.items
    val total = items.size
    val done = items.count { it.done }
    val active = total - done
    val rate = if (total == 0) 0 else (done * 100 / total)

    val byCategory = remember(items) {
        TodoCategory.entries.map { c ->
            val list = items.filter { it.category == c }
            c to (list.count { it.done } to list.size)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("통계") },
                navigationIcon = {
                    IconButton(onClick = onBackHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false, onClick = onBackHome,
                    icon = { Icon(Icons.Filled.Home, null) }, label = { Text("홈") }
                )
                NavigationBarItem(
                    selected = true, onClick = {},
                    icon = { Icon(Icons.Filled.Assessment, null) }, label = { Text("통계") }
                )
                NavigationBarItem(
                    selected = false, onClick = onOpenSettings,
                    icon = { Icon(Icons.Filled.Settings, null) }, label = { Text("설정") }
                )
            }
        }
    ) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(shape = MaterialTheme.shapes.extraLarge) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("전체 완료율", style = MaterialTheme.typography.titleMedium)
                    Text("할 일 달성 현황", color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        AnimatedDonut(
                            progress = if (total == 0) 0f else done.toFloat() / total,
                            diameter = 120.dp, stroke = 16.dp
                        )
                        Spacer(Modifier.width(16.dp))
                        AnimatedPercentText(target = rate)
                    }
                    Spacer(Modifier.height(8.dp))
                    AssistChip(onClick = {}, label = { Text("$done/$total 완") })
                }
            }

            Text("통계", style = MaterialTheme.typography.titleMedium)

            Card(shape = MaterialTheme.shapes.extraLarge) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("완료 현황", style = MaterialTheme.typography.titleMedium)
                    Text("완료 및 미완료 비율", color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    val doneActive = listOf(
                        Segment(done.toFloat(), MaterialTheme.colorScheme.primary),
                        Segment(active.toFloat(), MaterialTheme.colorScheme.surfaceVariant)
                    )
                    AnimatedPie(segments = doneActive, diameter = 180.dp)
                }
            }

            Card(shape = MaterialTheme.shapes.extraLarge) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("카테고리별 분포", style = MaterialTheme.typography.titleMedium)
                    Text("카테고리별 할 일 비율", color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    val palette = listOf(
                        Color(0xFFF1A33B),
                        MaterialTheme.colorScheme.primary,
                        Color(0xFF26A69A),
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                    val catSegments = TodoCategory.entries.mapIndexed { i, c ->
                        Segment(items.count { it.category == c }.toFloat(), palette[i % palette.size])
                    }
                    AnimatedDonut(segments = catSegments, diameter = 180.dp, stroke = 20.dp)
                    Spacer(Modifier.height(12.dp))
                    FlowLegend(
                        entries = TodoCategory.entries.mapIndexed { i, c ->
                            LegendEntry(catSegments[i].color, c.name, items.count { it.category == c })
                        }
                    )
                }
            }

            Card(shape = MaterialTheme.shapes.extraLarge) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("카테고리 상세", style = MaterialTheme.typography.titleMedium)
                    byCategory.forEach { (cat, pair) ->
                        val (d, all) = pair
                        val p = if (all == 0) 0f else d.toFloat() / all
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(cat.name, modifier = Modifier.width(56.dp))
                            AnimatedLinearProgress(progress = p, modifier = Modifier.weight(1f))
                            Spacer(Modifier.width(8.dp))
                            Text("$d/$all", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

private data class Segment(val value: Float, val color: Color)
private data class LegendEntry(val color: Color, val label: String, val value: Int)

@Composable
private fun AnimatedPie(segments: List<Segment>, diameter: Dp) {
    val total = segments.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
    val anim = remember { Animatable(0f) }
    LaunchedEffect(segments) { anim.snapTo(0f); anim.animateTo(1f, tween(900, easing = EaseInOut)) }
    Canvas(Modifier.size(diameter)) {
        var start = -90f
        val sizePx = diameter.toPx()
        val rect = Size(sizePx, sizePx)
        segments.forEach { s ->
            val sweep = 360f * (s.value / total) * anim.value
            drawArc(color = s.color, startAngle = start, sweepAngle = sweep, useCenter = true, size = rect)
            start += 360f * (s.value / total)
        }
    }
}

@Composable
private fun AnimatedDonut(
    progress: Float? = null,
    segments: List<Segment>? = null,
    diameter: Dp,
    stroke: Dp
) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(progress, segments) {
        anim.snapTo(0f)
        anim.animateTo(1f, tween(900, easing = EaseInOut))
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(Modifier.size(diameter)) {
        val sizePx = diameter.toPx()
        val rect = Size(sizePx, sizePx)
        val st = stroke.toPx()
        if (!segments.isNullOrEmpty()) {
            val total = segments.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
            var start = -90f
            segments.forEach { s ->
                val sweep = 360f * (s.value / total) * anim.value
                drawArc(
                    color = s.color, startAngle = start, sweepAngle = sweep,
                    useCenter = false, size = rect, style = Stroke(width = st, cap = StrokeCap.Round)
                )
                start += 360f * (s.value / total)
            }
        } else {
            val sweep = 360f * (progress ?: 0f) * anim.value
            drawArc(
                color = surfaceVariantColor,
                startAngle = -90f, sweepAngle = 360f, useCenter = false, size = rect,
                style = Stroke(width = st, cap = StrokeCap.Round)
            )
            drawArc(
                color = primaryColor,
                startAngle = -90f, sweepAngle = sweep, useCenter = false, size = rect,
                style = Stroke(width = st, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun FlowLegend(entries: List<LegendEntry>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        entries.forEach {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = it.color, shape = MaterialTheme.shapes.small, modifier = Modifier.size(10.dp)) {}
                Spacer(Modifier.width(8.dp))
                Text("${it.label}  ${it.value}개", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun AnimatedPercentText(target: Int) {
    val num = remember { Animatable(0f) }
    LaunchedEffect(target) { num.snapTo(0f); num.animateTo(target.toFloat(), tween(900, easing = EaseInOut)) }
    Text(
        text = "${num.value.toInt()}%",
        style = MaterialTheme.typography.displaySmall.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold
        )
    )
}

@Composable
private fun AnimatedLinearProgress(progress: Float, modifier: Modifier = Modifier) {
    val anim by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(700, easing = EaseInOut)
    )
    LinearProgressIndicator(
        progress = { anim },
        modifier = modifier.height(10.dp),
        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    )
}
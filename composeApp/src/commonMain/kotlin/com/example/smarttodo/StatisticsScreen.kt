@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import kotlinx.datetime.*

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
    
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val todayCount = items.count { it.due == today }

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
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            
            // 요약 통계 카드 3개
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "오늘",
                    value = todayCount.toString(),
                    color = Color(0xFF6366F1),
                    icon = Icons.Filled.CalendarMonth,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "진행",
                    value = active.toString(),
                    color = Color(0xFF10B981),
                    icon = Icons.Filled.Assessment,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "완료율",
                    value = "$rate%",
                    color = Color(0xFFF59E0B),
                    icon = Icons.Filled.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
            }

            // 전체 완료율 카드
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "전체 완료율",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "할 일 달성 현황",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedDonut(
                            progress = if (total == 0) 0f else done.toFloat() / total,
                            diameter = 140.dp,
                            stroke = 18.dp
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AnimatedPercentText(target = rate)
                            Text(
                                "$done / $total",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 완료 현황 카드
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "완료 현황",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "완료 및 미완료 비율",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    val doneActive = listOf(
                        Segment(done.toFloat(), MaterialTheme.colorScheme.primary),
                        Segment(active.toFloat(), MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f))
                    )
                    
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedPie(segments = doneActive, diameter = 200.dp)
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem(
                            color = MaterialTheme.colorScheme.primary,
                            label = "완료",
                            value = done
                        )
                        LegendItem(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                            label = "진행중",
                            value = active
                        )
                    }
                }
            }

            // 카테고리별 분포 카드
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "카테고리별 분포",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "카테고리별 할 일 비율",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    val palette = listOf(
                        Color(0xFF6366F1), // 인디고
                        Color(0xFF10B981), // 에메랄드
                        Color(0xFFF59E0B), // 앰버
                        Color(0xFF8B5CF6)  // 바이올렛
                    )
                    val catSegments = TodoCategory.entries.mapIndexed { i, c ->
                        Segment(items.count { it.category == c }.toFloat(), palette[i % palette.size])
                    }
                    
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedDonut(segments = catSegments, diameter = 200.dp, stroke = 24.dp)
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    FlowLegend(
                        entries = TodoCategory.entries.mapIndexed { i, c ->
                            LegendEntry(catSegments[i].color, c.name, items.count { it.category == c })
                        }
                    )
                }
            }

            // 카테고리 상세 카드
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "카테고리 상세",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    byCategory.forEachIndexed { index, (cat, pair) ->
                        val (d, all) = pair
                        val p = if (all == 0) 0f else d.toFloat() / all
                        val catColor = when (cat) {
                            TodoCategory.학업 -> Color(0xFF6366F1)
                            TodoCategory.업무 -> Color(0xFF10B981)
                            TodoCategory.개인 -> Color(0xFFF59E0B)
                            TodoCategory.기타 -> Color(0xFF8B5CF6)
                        }
                        
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = MaterialTheme.shapes.small,
                                        color = catColor.copy(alpha = 0.15f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                cat.name.first().toString(),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = catColor
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        cat.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    "$d / $all",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            AnimatedLinearProgress(
                                progress = p,
                                modifier = Modifier.fillMaxWidth(),
                                color = catColor
                            )
                        }
                        
                        if (index < byCategory.size - 1) {
                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

// 데이터 클래스
private data class Segment(val value: Float, val color: Color)
private data class LegendEntry(val color: Color, val label: String, val value: Int)

// 통계 카드 컴포넌트
@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = modifier
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Text(
                value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = color.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// 파이 차트
@Composable
private fun AnimatedPie(segments: List<Segment>, diameter: Dp) {
    val total = segments.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
    val anim = remember { Animatable(0f) }
    LaunchedEffect(segments) {
        anim.snapTo(0f)
        anim.animateTo(1f, tween(900, easing = EaseInOut))
    }
    Canvas(Modifier.size(diameter)) {
        var start = -90f
        val sizePx = diameter.toPx()
        val rect = Size(sizePx, sizePx)
        segments.forEach { s ->
            val sweep = 360f * (s.value / total) * anim.value
            drawArc(
                color = s.color,
                startAngle = start,
                sweepAngle = sweep,
                useCenter = true,
                size = rect
            )
            start += 360f * (s.value / total)
        }
    }
}

// 도넛 차트
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
                    color = s.color,
                    startAngle = start,
                    sweepAngle = sweep,
                    useCenter = false,
                    size = rect,
                    style = Stroke(width = st, cap = StrokeCap.Round)
                )
                start += 360f * (s.value / total)
            }
        } else {
            val sweep = 360f * (progress ?: 0f) * anim.value
            drawArc(
                color = surfaceVariantColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                size = rect,
                style = Stroke(width = st, cap = StrokeCap.Round)
            )
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                size = rect,
                style = Stroke(width = st, cap = StrokeCap.Round)
            )
        }
    }
}

// 범례
@Composable
private fun FlowLegend(entries: List<LegendEntry>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        entries.forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Surface(
                    color = it.color,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(12.dp),
                    shadowElevation = 1.dp
                ) {}
                Spacer(Modifier.width(12.dp))
                Text(
                    "${it.label}  ${it.value}개",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// 범례 아이템
@Composable
private fun LegendItem(
    color: Color,
    label: String,
    value: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = color,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.size(16.dp)
        ) {}
        Column {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "${value}개",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// 애니메이션 퍼센트 텍스트
@Composable
private fun AnimatedPercentText(target: Int) {
    val num = remember { Animatable(0f) }
    LaunchedEffect(target) {
        num.snapTo(0f)
        num.animateTo(target.toFloat(), tween(900, easing = EaseInOut))
    }
    Text(
        text = "${num.value.toInt()}%",
        style = MaterialTheme.typography.displaySmall.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold
        )
    )
}

// 애니메이션 선형 진행바
@Composable
private fun AnimatedLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val anim by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(700, easing = EaseInOut)
    )
    LinearProgressIndicator(
        progress = { anim },
        modifier = modifier.height(12.dp),
        trackColor = color.copy(alpha = 0.12f),
        color = color
    )
}

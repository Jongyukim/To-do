@file:OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.smarttodo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private data class IntroPage(
    val icon: @Composable () -> Unit,
    val badgeColor: Color,
    val title: String,
    val subtitle: String
)

@Composable
fun OnboardingScreens(
    onSkip: () -> Unit,
    onFinish: () -> Unit
) {
    val pages = remember {
        listOf(
            IntroPage(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List, // deprec 대체
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                badgeColor = Color(0xFF6C63FF),
                title = "할 일을 쉽게 정리하세요",
                subtitle = "간단한 인터페이스로 오늘 할 일을 빠르게 정리하세요."
            ),
            IntroPage(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                badgeColor = Color(0xFF58B6A9),
                title = "알림으로 놓치지 마세요",
                subtitle = "중요한 일정은 리마인더 알림으로 알려드려요."
            ),
            IntroPage(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Assessment,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                badgeColor = Color(0xFFF1A33B),
                title = "진행 상황을 확인하세요",
                subtitle = "통계를 통해 달성도와 패턴을 한눈에 파악하세요."
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = { TextButton(onClick = onSkip) { Text("건너뛰기") } }
            )
        },
        bottomBar = {
            BottomBar(
                isLast = pagerState.currentPage == pages.lastIndex,
                onNext = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else onFinish()
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                IntroPageView(pages[page])
            }

            Spacer(Modifier.height(12.dp))
            DotsIndicator(total = pages.size, selectedIndex = pagerState.currentPage)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun IntroPageView(page: IntroPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // 상단 아이콘 배지
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(page.badgeColor),
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.size(56.dp)) { page.icon() }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(page.title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(page.subtitle, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun DotsIndicator(total: Int, selectedIndex: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { idx ->
            val w = if (idx == selectedIndex) 28.dp else 8.dp
            val alpha = if (idx == selectedIndex) 1f else 0.35f
            Box(
                Modifier
                    .height(6.dp)
                    .width(w)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
            )
        }
    }
}

@Composable
private fun BottomBar(
    isLast: Boolean,
    onNext: () -> Unit
) {
    Surface(shadowElevation = 8.dp) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(if (isLast) "시작하기" else "다음")
            }
        }
    }
}
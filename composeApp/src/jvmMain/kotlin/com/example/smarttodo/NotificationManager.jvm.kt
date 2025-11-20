package com.example.smarttodo

import kotlinx.datetime.*

/**
 * Desktop (JVM) 알림 관리자 구현체
 * Desktop에서는 알림 기능을 지원하지 않으므로 더미 구현을 제공합니다.
 */
class JvmNotificationManagerImpl : NotificationManager {
    override suspend fun scheduleNotification(todo: Todo): Boolean {
        // Desktop에서는 알림을 지원하지 않음
        return false
    }

    override suspend fun cancelNotification(todoId: String) {
        // Desktop에서는 알림을 지원하지 않음
    }

    override suspend fun cancelAllNotifications() {
        // Desktop에서는 알림을 지원하지 않음
    }

    override suspend fun hasPermission(): Boolean {
        // Desktop에서는 항상 false 반환
        return false
    }

    override suspend fun requestPermission() {
        // Desktop에서는 알림을 지원하지 않음
    }
}

/**
 * 알림 시간 계산 (JVM/Desktop)
 * due가 null이면 오늘 날짜를 사용합니다.
 */
actual fun calculateNotificationTime(due: LocalDate?, remindTime: String?): Long? {
    if (remindTime == null) return null

    try {
        val (hour, minute) = remindTime.split(":").map { it.toInt() }
        
        // due가 null이면 오늘 날짜 사용
        val targetDate = due ?: run {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            now.date
        }
        
        val timeZone = TimeZone.currentSystemDefault()
        val dateTime = LocalDateTime(
            year = targetDate.year,
            monthNumber = targetDate.monthNumber,
            dayOfMonth = targetDate.dayOfMonth,
            hour = hour,
            minute = minute,
            second = 0
        )
        
        val instant = dateTime.toInstant(timeZone)
        return instant.toEpochMilliseconds()
    } catch (e: Exception) {
        return null
    }
}

actual fun getNotificationManager(): NotificationManager {
    return JvmNotificationManagerImpl()
}


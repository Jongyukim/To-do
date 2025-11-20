package com.example.smarttodo

import kotlinx.datetime.LocalDate

/**
 * 알림 관리자 인터페이스
 * 플랫폼별로 다른 구현체를 제공합니다.
 */
interface NotificationManager {
    /**
     * 알림을 스케줄링합니다.
     * @param todo 알림을 설정할 할 일
     * @return 성공 여부
     */
    suspend fun scheduleNotification(todo: Todo): Boolean

    /**
     * 알림을 취소합니다.
     * @param todoId 취소할 할 일의 ID
     */
    suspend fun cancelNotification(todoId: String)

    /**
     * 모든 알림을 취소합니다.
     */
    suspend fun cancelAllNotifications()

    /**
     * 알림 권한이 허용되었는지 확인합니다.
     */
    suspend fun hasPermission(): Boolean

    /**
     * 알림 권한을 요청합니다.
     */
    suspend fun requestPermission()
}

/**
 * 알림 시간을 파싱하여 마감일과 결합하여 실제 알림 시간을 계산합니다.
 * @param due 마감일 (null이면 오늘 날짜 사용)
 * @param remindTime 알림 시간 (HH:mm 형식)
 * @return 알림이 발생해야 하는 날짜와 시간 (밀리초 타임스탬프)
 */
expect fun calculateNotificationTime(due: LocalDate?, remindTime: String?): Long?

/**
 * 플랫폼별 NotificationManager 인스턴스를 가져옵니다.
 */
expect fun getNotificationManager(): NotificationManager


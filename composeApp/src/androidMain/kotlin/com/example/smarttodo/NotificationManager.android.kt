package com.example.smarttodo

import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import java.util.concurrent.TimeUnit

/**
 * Android 알림 관리자 구현체
 * WorkManager를 사용하여 알림을 스케줄링합니다.
 */
class AndroidNotificationManagerImpl(
    private val context: Context
) : NotificationManager {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationWorker.CHANNEL_ID,
                NotificationWorker.CHANNEL_NAME,
                AndroidNotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = NotificationWorker.CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override suspend fun scheduleNotification(todo: Todo): Boolean = withContext(Dispatchers.IO) {
        if (!todo.remind || todo.done) return@withContext false

        val notificationTime = calculateNotificationTime(todo.due, todo.remindTime)
            ?: return@withContext false

        val currentTime = System.currentTimeMillis()
        if (notificationTime <= currentTime) return@withContext false

        val delay = notificationTime - currentTime

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "todoId" to todo.id,
                    "todoTitle" to todo.title,
                    "todoCategory" to todo.category.name
                )
            )
            .addTag("todo_${todo.id}")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        true
    }

    override suspend fun cancelNotification(todoId: String): Unit = withContext(Dispatchers.IO) {
        WorkManager.getInstance(context).cancelAllWorkByTag("todo_$todoId")
        Unit
    }

    override suspend fun cancelAllNotifications(): Unit = withContext(Dispatchers.IO) {
        WorkManager.getInstance(context).cancelAllWork()
        Unit
    }

    override suspend fun hasPermission(): Boolean = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationManager.areNotificationsEnabled()
        } else {
            true // Android 12 이하는 기본적으로 권한이 있음
        }
    }

    override suspend fun requestPermission() {
        // 권한 요청은 Activity에서 처리해야 하므로 여기서는 빈 구현
        // 실제 권한 요청은 Compose에서 처리
    }
}

/**
 * 알림 시간 계산 (Android)
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

// Application Context를 저장할 변수
private var applicationContext: Context? = null

/**
 * NotificationManager 초기화 (MainActivity에서 호출)
 */
fun initNotificationManager(context: Context) {
    applicationContext = context.applicationContext
}

actual fun getNotificationManager(): NotificationManager {
    val context = applicationContext
        ?: throw IllegalStateException("NotificationManager가 초기화되지 않았습니다. MainActivity.onCreate()에서 initNotificationManager()를 호출하세요.")
    return AndroidNotificationManagerImpl(context)
}


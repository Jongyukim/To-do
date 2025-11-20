package com.example.smarttodo

import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * WorkManager를 사용한 알림 워커
 */
class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val todoId = inputData.getString("todoId") ?: return Result.failure()
        val todoTitle = inputData.getString("todoTitle") ?: return Result.failure()
        val todoCategory = inputData.getString("todoCategory") ?: ""

        showNotification(todoId, todoTitle, todoCategory)
        return Result.success()
    }

    private fun showNotification(todoId: String, title: String, category: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager

        // 알림 채널 생성 (Android 8.0 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                AndroidNotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 클릭 시 앱 열기
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("todoId", todoId)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            todoId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("할 일 알림")
            .setContentText("$title ($category)")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$title\n카테고리: $category\n할 일을 확인하세요!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(todoId.hashCode(), notification)
    }

    companion object {
        const val CHANNEL_ID = "todo_notifications"
        const val CHANNEL_NAME = "할 일 알림"
        const val CHANNEL_DESCRIPTION = "할 일 마감일 및 알림 시간 알림"
    }
}


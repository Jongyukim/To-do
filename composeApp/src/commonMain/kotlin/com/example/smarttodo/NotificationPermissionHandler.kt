package com.example.smarttodo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * 알림 권한 상태를 관리하는 Composable
 */
@Composable
fun NotificationPermissionHandler(
    notificationManager: NotificationManager?,
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
) {
    var hasPermission by remember { mutableStateOf<Boolean?>(null) }
    
    LaunchedEffect(notificationManager) {
        notificationManager?.let {
            hasPermission = it.hasPermission()
            if (hasPermission == true) {
                onPermissionGranted()
            } else if (hasPermission == false) {
                onPermissionDenied()
            }
        }
    }
}



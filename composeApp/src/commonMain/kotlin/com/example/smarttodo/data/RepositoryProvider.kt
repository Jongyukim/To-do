package com.example.smarttodo.data

/**
 * Kotlin Multiplatform 환경에서 FirebaseRepository 인스턴스를 제공하기 위한 expect 함수.
 * 각 플랫폼별 (Android, JVM) 실제 구현은 `actual` 키워드를 사용하여 제공해야 합니다.
 */
expect fun getFirebaseRepository(): FirebaseRepository
package com.example.smarttodo.data

/**
 * Android 플랫폼을 위한 getFirebaseRepository 함수의 실제 구현체.
 * Android에서는 FirebaseRepository의 인스턴스를 직접 생성하여 반환합니다.
 */
actual fun getFirebaseRepository(): FirebaseRepository = FirebaseRepository()
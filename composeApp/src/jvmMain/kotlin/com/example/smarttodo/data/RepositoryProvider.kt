package com.example.smarttodo.data

/**
 * JVM (Desktop) 플랫폼을 위한 getFirebaseRepository 함수의 실제 구현체.
 * Desktop에서는 FirebaseRepository의 인스턴스를 직접 생성하여 반환합니다.
 *
 * 참고: Desktop용 FirebaseRepository 자체는 현재 플레이스홀더 구현입니다.
 */
actual fun getFirebaseRepository(): FirebaseRepository = FirebaseRepository()
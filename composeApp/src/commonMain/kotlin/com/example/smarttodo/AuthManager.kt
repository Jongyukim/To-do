package com.example.smarttodo

/**
 * Firebase 인증 기능을 추상화하는 expect 클래스.
 * 각 플랫폼별로 실제 구현을 제공해야 합니다.
 */
expect class AuthManager {
    /**
     * 이메일과 비밀번호로 사용자 로그인.
     * @return 로그인 성공 시 true, 실패 시 false.
     */
    suspend fun login(email: String, password: String): Boolean

    /**
     * 이메일과 비밀번호로 사용자 등록.
     * @return 등록 성공 시 true, 실패 시 false.
     */
    suspend fun register(email: String, password: String, displayName: String? = null): Boolean

    /**
     * 현재 로그인된 사용자의 UID를 반환.
     * @return 로그인된 사용자의 UID 또는 null.
     */
    fun getCurrentUserId(): String?

    fun getCurrentUserEmail(): String?

    fun getCurrentUserDisplayName(): String?


    /**
     * 현재 사용자가 로그인되어 있는지 확인.
     */
    fun isLoggedIn(): Boolean

    fun signOut()
}

/**
 * AuthManager의 인스턴스를 가져오는 expect 함수.
 * 각 플랫폼별로 실제 구현을 제공해야 합니다.
 */
expect fun getAuthManager(): AuthManager

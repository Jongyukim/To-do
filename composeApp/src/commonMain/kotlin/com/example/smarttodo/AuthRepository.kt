package com.example.smarttodo

interface AuthRepository {
    // 로그인
    suspend fun signIn(email: String, password: String): Result<User>

    // 회원가입
    suspend fun signUp(name: String, email: String, password: String): Result<User>

    // 로그아웃
    suspend fun signOut(): Result<Unit>

    // 앱 시작 시 현재 로그인 유저 가져오기
    suspend fun getCurrentUser(): User?
}

package com.example.smarttodo

class FakeAuthRepository : AuthRepository {
    private var currentUser: User? = null

    override suspend fun signIn(email: String, password: String): Result<User> {
        // 간단 검증만 하는 임시 로그인 (나중에 Firebase로 교체 가능)
        return if (email.isNotBlank() && password.length >= 4) {
            val user = User(
                id = email,                 // 임시로 이메일을 ID처럼 사용
                email = email,
                name = email.substringBefore("@").ifBlank { "사용자" }
            )
            currentUser = user
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("이메일 또는 비밀번호를 확인하세요."))
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): Result<User> {
        return if (name.isNotBlank() && email.isNotBlank() && password.length >= 8) {
            val user = User(
                id = email,
                email = email,
                name = name
            )
            currentUser = user
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("회원가입 정보를 확인하세요."))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        currentUser = null
        return Result.success(Unit)
    }

    override suspend fun getCurrentUser(): User? = currentUser
}

package com.example.smarttodo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

actual class AuthManager {
    private val auth = FirebaseAuth.getInstance()

    actual suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            println("Login failed: ${e.message}")
            false
        }
    }

    actual suspend fun register(email: String, password: String, displayName: String?): Boolean {
        return try {
            println("AuthManager: Attempting to register user with email: $email, displayName: $displayName")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            if (displayName != null && result.user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                result.user!!.updateProfile(profileUpdates).await()
                println("AuthManager: User profile updated with displayName: $displayName")
            } else if (displayName == null) {
                println("AuthManager: No displayName provided for registration.")
            }
            println("AuthManager: User registered successfully.")
            true
        } catch (e: Exception) {
            println("AuthManager: Register failed: ${e.message}")
            false
        }
    }

    actual fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    actual fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    actual fun getCurrentUserDisplayName(): String? {
        return auth.currentUser?.displayName
    }

    actual fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    actual fun signOut() {
        auth.signOut()
    }
}

actual fun getAuthManager(): AuthManager = AuthManager()

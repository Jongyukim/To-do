package com.example.smarttodo.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Android 플랫폼을 위한 FirebaseRepository의 실제 구현체.
 */
actual class FirebaseRepository {

    // Firestore 인스턴스를 가져옵니다.
    private val db = Firebase.firestore

    // 현재 로그인된 사용자의 ID를 가져옵니다. 사용자가 없으면 예외가 발생할 수 있으므로 주의.
    private val userId: String
        get() = Firebase.auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    /**
     * 현재 사용자의 모든 할 일 목록을 Firestore에서 가져옵니다.
     */
    actual suspend fun getAllTodos(): List<FirestoreTodo> {
        return try {
            val snapshot = db.collection("users").document(userId).collection("todos")
                .get()
                .await()
            // Firestore 문서를 FirestoreTodo 데이터 클래스 리스트로 변환합니다.
            snapshot.toObjects(FirestoreTodo::class.java)
        } catch (e: Exception) {
            // 오류 발생 시 로그를 남기거나 오류 처리를 합니다.
            println("Error getting todos: ${e.message}")
            emptyList()
        }
    }

    /**
     * 새로운 할 일을 Firestore에 추가합니다.
     */
    actual suspend fun addTodo(todo: FirestoreTodo) {
        val todoWithUserId = todo.copy(userId = userId)
        db.collection("users").document(userId).collection("todos")
            .document(todoWithUserId.id)
            .set(todoWithUserId)
            .await()
    }

    /**
     * ID로 특정 할 일 정보를 Firestore에서 가져옵니다.
     */
    actual suspend fun getTodo(id: String): FirestoreTodo? {
        return try {
            val snapshot = db.collection("users").document(userId).collection("todos")
                .document(id)
                .get()
                .await()
            snapshot.toObject(FirestoreTodo::class.java)
        } catch (e: Exception) {
            println("Error getting todo by id: ${e.message}")
            null
        }
    }

    /**
     * 기존 할 일 정보를 Firestore에서 업데이트합니다.
     */
    actual suspend fun updateTodo(todo: FirestoreTodo) {
        db.collection("users").document(userId).collection("todos")
            .document(todo.id)
            .set(todo) // set을 사용하면 문서 전체를 덮어씁니다. 부분 업데이트는 update()를 사용합니다.
            .await()
    }

    /**
     * ID로 특정 할 일을 Firestore에서 삭제합니다.
     */
    actual suspend fun deleteTodo(id: String) {
        db.collection("users").document(userId).collection("todos")
            .document(id)
            .delete()
            .await()
    }
}

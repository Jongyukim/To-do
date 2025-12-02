package com.example.smarttodo.data

import com.example.smarttodo.TodoCategory

/**
 * Firestore와 직접 통신하는 저장소 클래스에 대한 `expect` 선언.
 * 공통 코드는 이 추상화된 저장소를 사용하며, 실제 구현은 각 플랫폼에서 제공합니다.
 */
expect class FirebaseRepository {

    /**
     * 새로운 할 일을 Firestore에 추가합니다. (Create)
     */
    suspend fun addTodo(todo: FirestoreTodo)

    /**
     * ID로 특정 할 일 정보를 Firestore에서 가져옵니다. (Read)
     */
    suspend fun getTodo(id: String): FirestoreTodo?

    /**
     * 현재 사용자의 모든 할 일 목록을 Firestore에서 가져옵니다. (Read)
     */
    suspend fun getAllTodos(): List<FirestoreTodo>

    /**
     * 기존 할 일 정보를 Firestore에서 업데이트합니다. (Update)
     */
    suspend fun updateTodo(todo: FirestoreTodo)

    /**
     * ID로 특정 할 일을 Firestore에서 삭제합니다. (Delete)
     */
    suspend fun deleteTodo(id: String)
}


/**
 * Firestore에 저장하기 위한 Todo 데이터 클래스.
 * 복잡한 타입(LocalDate, enum) 대신 기본 타입(String)을 사용하여
 * 멀티플랫폼 환경에서 Firestore의 직렬화/역직렬화 문제를 방지합니다.
 */
data class FirestoreTodo(
    val id: String = "",
    val title: String = "",
    val category: String = TodoCategory.개인.name,
    val due: String? = null, // "yyyy-MM-dd" 형식의 문자열
    val remind: Boolean = false,
    val remindTime: String? = null, // "HH:mm"
    val memo: String = "",
    val done: Boolean = false,
    val userId: String = "" // 데이터 소유자를 식별하기 위한 필드
)

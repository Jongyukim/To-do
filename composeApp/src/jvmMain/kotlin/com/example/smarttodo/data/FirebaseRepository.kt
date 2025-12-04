package com.example.smarttodo.data

/**
 * JVM (Desktop) 플랫폼을 위한 FirebaseRepository의 임시 실제 구현체.
 *
 * 참고: 데스크탑 클라이언트에서 Firebase 사용자 인증을 안전하게 처리하는 것은 복잡하며,
 * 현재 프로젝트의 라이브러리 구성으로는 한계가 있습니다.
 * 따라서 여기서는 앱이 컴파일되도록 임시 코드를 작성합니다.
 * Android 앱의 기능 개발을 우선으로 진행하는 것을 권장합니다.
 */
actual class FirebaseRepository {

    init {
        println("경고: JVM용 FirebaseRepository는 아직 구현되지 않았습니다. 모든 데이터 작업은 시뮬레이션됩니다.")
    }

    actual suspend fun getAllTodos(): List<FirestoreTodo> {
        println("JVM: getAllTodos() 호출됨 (구현 없음)")
        return emptyList()
    }

    actual suspend fun addTodo(todo: FirestoreTodo) {
        println("JVM: addTodo() 호출됨 (구현 없음)")
        // No-op
    }

    actual suspend fun getTodo(id: String): FirestoreTodo? {
        println("JVM: getTodo(id: $id) 호출됨 (구현 없음)")
        return null
    }

    actual suspend fun updateTodo(todo: FirestoreTodo) {
        println("JVM: updateTodo() 호출됨 (구현 없음)")
        // No-op
    }

    actual suspend fun deleteTodo(id: String) {
        println("JVM: deleteTodo(id: $id) 호출됨 (구현 없음)")
        // No-op
    }
}

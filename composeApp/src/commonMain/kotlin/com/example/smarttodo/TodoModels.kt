package com.example.smarttodo

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

enum class TodoCategory { 학업, 업무, 개인, 기타 }

data class Todo(
    val id: String,
    val title: String,
    val category: TodoCategory = TodoCategory.개인,
    val due: LocalDate? = null,
    val remind: Boolean = false,
    val remindTime: String? = null, // UI만: "HH:mm"
    val memo: String = "",
    val done: Boolean = false
)

class TodoStore(
    private val notificationManager: NotificationManager? = null
) {
    private val _items: SnapshotStateList<Todo> = mutableStateListOf()
    val items: List<Todo> get() = _items
    
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun add(t: Todo) {
        _items.add(0, t)
        // 알림 스케줄링
        if (t.remind && !t.done) {
            notificationManager?.let { manager ->
                coroutineScope.launch {
                    manager.scheduleNotification(t)
                }
            }
        }
    }
    
    fun update(id: String, edit: (Todo) -> Todo) {
        val i = _items.indexOfFirst { it.id == id }
        if (i >= 0) {
            val oldTodo = _items[i]
            val newTodo = edit(oldTodo)
            _items[i] = newTodo
            
            // 알림 업데이트
            notificationManager?.let { manager ->
                coroutineScope.launch {
                    // 기존 알림 취소
                    manager.cancelNotification(id)
                    // 새 알림 스케줄링 (remind가 true이고 done이 false인 경우)
                    if (newTodo.remind && !newTodo.done) {
                        manager.scheduleNotification(newTodo)
                    }
                }
            }
        }
    }
    
    fun remove(id: String) {
        _items.removeAll { it.id == id }
        // 알림 취소
        notificationManager?.let { manager ->
            coroutineScope.launch {
                manager.cancelNotification(id)
            }
        }
    }
    
    fun toggleDone(id: String) {
        val i = _items.indexOfFirst { it.id == id }
        if (i >= 0) {
            val oldTodo = _items[i]
            val newTodo = oldTodo.copy(done = !oldTodo.done)
            _items[i] = newTodo
            
            // 완료 시 알림 취소, 미완료로 변경 시 알림 재스케줄링
            notificationManager?.let { manager ->
                coroutineScope.launch {
                    if (newTodo.done) {
                        // 완료 시 알림 취소
                        manager.cancelNotification(id)
                    } else if (newTodo.remind) {
                        // 미완료로 변경하고 remind가 true면 알림 재스케줄링
                        manager.cancelNotification(id)
                        manager.scheduleNotification(newTodo)
                    }
                }
            }
        }
    }
}
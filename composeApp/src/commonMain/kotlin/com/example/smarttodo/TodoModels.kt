package com.example.smarttodo

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import jdk.jfr.internal.consumer.EventLog.update
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

class TodoStore {
    private val _items: SnapshotStateList<Todo> = mutableStateListOf()
    val items: List<Todo> get() = _items

    fun add(t: Todo) { _items.add(0, t) }
    fun update(id: String, edit: (Todo) -> Todo) {
        val i = _items.indexOfFirst { it.id == id }
        if (i >= 0) _items[i] = edit(_items[i])
    }
    fun remove(id: String) { _items.removeAll { it.id == id } }
    fun toggleDone(id: String) = update(id) { it.copy(done = !it.done) }
}
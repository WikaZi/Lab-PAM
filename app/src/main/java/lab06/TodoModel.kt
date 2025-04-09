package lab06

import android.R.attr.id
import java.time.LocalDate

fun todoTasks(): List<TodoTask> {
    return listOf(
        TodoTask("Programming", LocalDate.of(2024, 4, 18), false, Priority.Low, 1),
        TodoTask("Teaching", LocalDate.of(2024, 5, 12), false, Priority.High, 2),
        TodoTask("Learning", LocalDate.of(2024, 6, 28), true, Priority.Low, 3),
        TodoTask("Cooking", LocalDate.of(2024, 8, 18), false, Priority.Medium, 4),
    )
}

enum class Priority {
    High, Medium, Low
}

data class TodoTask(
    val title: String,
    val deadline: LocalDate,
    val isDone: Boolean,
    val priority: Priority,
    val id: Int
)

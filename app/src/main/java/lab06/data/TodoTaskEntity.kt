package lab06.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import lab06.Priority
import lab06.TodoTask
import java.time.LocalDate

@Entity(tableName = "tasks")
data class TodoTaskEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String = "",
    var deadline: LocalDate = LocalDate.now(),
    var isDone: Boolean = false,
    var priority: Priority = Priority.Low
){
    fun toModel(): TodoTask {
        return TodoTask(
            id = id,
            deadline = deadline,
            isDone = isDone,
            priority = priority,
            title = title
        )
    }

    companion object {
        fun fromModel(model: TodoTask): TodoTaskEntity {
            return TodoTaskEntity(
                id = model.id,
                title = model.title,
                priority = model.priority,
                isDone = model.isDone,
                deadline = model.deadline
            )
        }
    }
}
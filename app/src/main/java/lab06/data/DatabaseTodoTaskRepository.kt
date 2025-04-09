package lab06.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import lab06.TodoTask
import lab06.todoTasks

class DatabaseTodoTaskRepository(private val dao: TodoTaskDao) : TodoTaskRepository {

    override fun getAllAsStream(): Flow<List<TodoTask>> {
//        return flow {
//            emit(todoTasks())
//        }
        return dao.findAll().map { it.map { entity -> entity.toModel() } }
    }

    override fun getItemAsStream(id: Int): Flow<TodoTask?> {
        return dao.find(id).map { it.toModel() }
//        return flow {
//            emit(todoTasks().find { it.id == id })
//        }
    }

    override suspend fun insertItem(item: TodoTask) {
        dao.insertAll(TodoTaskEntity.fromModel(item))
    }

    override suspend fun deleteItem(item: TodoTask) {
        dao.removeById(TodoTaskEntity.fromModel(item))
    }

    override suspend fun updateItem(item: TodoTask) {
        dao.insertAll(TodoTaskEntity.fromModel(item))
    }
}

package lab06.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoTaskDao {

    @Insert
    suspend fun insertAll(vararg tasks: TodoTaskEntity)

    @Delete
    suspend fun removeById(item: TodoTaskEntity)

    @Query("SELECT * FROM tasks")
    fun findAll(): Flow<List<TodoTaskEntity>>

    @Query("SELECT * FROM tasks WHERE id == :id")
    fun find(id: Int): Flow<TodoTaskEntity>
}
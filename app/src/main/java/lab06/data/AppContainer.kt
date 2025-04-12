package lab06.data

import android.content.Context
import lab06.NotificationHandler

interface AppContainer {
    val todoTaskRepository: TodoTaskRepository
    val currentDateProvider: CurrentDateProvider
        get() = DefaultCurrentDateProvider()
    val notificationHandler: NotificationHandler
}

class AppDataContainer(private val context: Context): AppContainer{
    override val todoTaskRepository: TodoTaskRepository by lazy {
        DatabaseTodoTaskRepository(AppDatabase.getInstance(context).taskDao())
    }
    override val notificationHandler: NotificationHandler by lazy {
        NotificationHandler(context)
    }
}
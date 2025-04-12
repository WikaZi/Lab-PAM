package lab06

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import lab06.viewmodel.FormViewModel
import lab06.viewmodel.ListViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        val container = TodoApplication.container
        initializer {
            ListViewModel(
                repository = container.todoTaskRepository
            )
        }
        initializer {
            FormViewModel(
                repository = container.todoTaskRepository,
                currentDateProvider =container.currentDateProvider
            )
        }
    }
}

fun CreationExtras.todoApplication(): TodoApplication {
    val app = this[APPLICATION_KEY]
    return app as TodoApplication
}

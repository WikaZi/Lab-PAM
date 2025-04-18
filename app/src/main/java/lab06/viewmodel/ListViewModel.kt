package lab06.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lab06.TodoTask
import lab06.data.TodoTaskRepository

class ListViewModel(val repository: TodoTaskRepository) : ViewModel() {
    val listUiState: StateFlow<ListUiState>
        get() {
            return repository.getAllAsStream().map { ListUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = ListUiState()
                )
        }
    fun deleteItem(task: TodoTask) {
        viewModelScope.launch {
            repository.deleteItem(task)
        }
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ListUiState(val items: List<TodoTask> = listOf())
package com.codinginflow.mvvmtodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.dao.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchValue = MutableStateFlow("")
    val preferencesFlow = preferencesManager.preferencesFlow
    private val taskEventChannel= Channel<TaskEvent>()
    val taskEventFlow = taskEventChannel.receiveAsFlow()

    private val taskFlow = combine(
        searchValue,
        preferencesFlow
    ) { query, preferencesFlow ->
        Pair(query, preferencesFlow)
    }.flatMapLatest { (query, preferencesFlow) ->
        taskDao.getTasks(query, preferencesFlow.sortOrder, preferencesFlow.hideCompleted)
    }
    val tasks = taskFlow.asLiveData()

    fun onSortOrderChanged(sortOrder: SortOrder)=viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedUpdate(hideCompleted : Boolean)=viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskCheckChanged(task: Task,isChecked : Boolean){
        viewModelScope.launch {
            taskDao.update(task.copy(completed = isChecked))
        }
    }

    fun onTaskClicked(task: Task){

    }

    fun onTaskSwiped(task: Task){
       viewModelScope.launch {
           taskDao.delete(task)
           taskEventChannel.send(TaskEvent.ShowUndoDeleteMessage(task))

       }
    }

    fun onUndoDeleteTask(task: Task){
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    sealed class TaskEvent{
        data class ShowUndoDeleteMessage(val task: Task) : TaskEvent()
    }
}
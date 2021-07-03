package com.codinginflow.mvvmtodo.ui.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.dao.TaskDao
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao : TaskDao,
    state : SavedStateHandle
) : ViewModel() {
    /*private val task = state.get<Task>("task")
    private val taskName=task?.name ?: ""*/
}
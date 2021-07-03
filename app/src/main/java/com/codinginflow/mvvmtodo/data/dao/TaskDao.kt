package com.codinginflow.mvvmtodo.data.dao

import androidx.room.*
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    fun getTasks(
        searchQuery: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_NAME -> getTasksSortedByName(searchQuery, hideCompleted)
            SortOrder.BY_DATE -> getTasksSortedByDate(searchQuery, hideCompleted)
        }

    @Query("SELECT * FROM TASK_TABLE WHERE(completed !=:hideCompleted OR completed=0) AND name LIKE '%' || :searchQuery || '%' ORDER BY priority DESC,name")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM TASK_TABLE WHERE(completed !=:hideCompleted OR completed=0) AND name LIKE '%' || :searchQuery || '%' ORDER BY priority DESC,created")
    fun getTasksSortedByDate(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>
}
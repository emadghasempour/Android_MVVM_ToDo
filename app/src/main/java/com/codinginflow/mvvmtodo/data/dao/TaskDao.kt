package com.codinginflow.mvvmtodo.data.dao

import androidx.room.*
import com.codinginflow.mvvmtodo.data.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task : Task)

    @Delete
    suspend fun delete(task :Task)

    @Query("SELECT * FROM TASK_TABLE")
    fun getTasks() : Flow<List<Task>>
}
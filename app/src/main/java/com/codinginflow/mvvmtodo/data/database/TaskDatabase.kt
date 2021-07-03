package com.codinginflow.mvvmtodo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.dao.TaskDao
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase(){
    abstract fun getDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val coroutineScope: CoroutineScope
    ) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // db operations
            val dao = database.get().getDao()

            coroutineScope.launch {
                dao.insert(Task("Remove unnecessary",priority = true))
                dao.insert(Task("Drive the video call",completed = true))
                dao.insert(Task("At home remove unnecessary",priority = true))
                dao.insert(Task("eat the food",completed = true))
                dao.insert(Task("Cook the food"))
            }
        }
    }
}
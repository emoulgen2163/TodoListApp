package com.mycompany.firstapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mycompany.firstapp.model.TaskListModel
import com.mycompany.firstapp.model.TaskModel
import kotlinx.coroutines.CoroutineScope

@Database([TaskModel::class, TaskListModel::class], version = 3)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TasksDao

    companion object{
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun createDatabase(context: Context): TaskDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, TaskDatabase::class.java, "task_db").build()
                INSTANCE = instance
                instance
            }
        }
    }
}
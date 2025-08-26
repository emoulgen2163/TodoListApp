package com.mycompany.firstapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mycompany.firstapp.model.TaskListModel
import com.mycompany.firstapp.model.TaskModel

@Dao
interface TasksDao {

    @Insert
    suspend fun addTask(task: TaskModel)

    @Insert
    suspend fun addTaskList(taskListModel: TaskListModel)

    @Delete
    suspend fun deleteTask(task: TaskModel)

    @Delete
    suspend fun deleteTaskList(taskListModel: TaskListModel)

    @Update
    suspend fun updateTask(task: TaskModel)

    @Update
    suspend fun updateTaskListModel(taskListModel: TaskListModel)

    @Query("SELECT * FROM TaskListModel")
    fun getAllTaskLists(): LiveData<List<TaskListModel>>

    @Query("SELECT * FROM TaskModel WHERE isTaskDone = 1 AND parentListName = :parentListName")
    fun getDoneTasks(parentListName: String): LiveData<List<TaskModel>>

    @Query("SELECT * FROM TaskModel WHERE isTaskDone = 0 AND parentListName = :parentListName")
    fun getAllTasks(parentListName: String): LiveData<List<TaskModel>>

    @Query("UPDATE TaskModel SET parentListName = :newName WHERE parentListName = :oldName")
    suspend fun updateParentListName(oldName: String, newName: String)

    @Query("DELETE FROM TaskModel WHERE parentListName = :parentListName")
    suspend fun deleteTaskByParentListName(parentListName: String)

    @Query("SELECT * FROM TaskModel WHERE name LIKE '%' || :queryName || '%'")
    fun searchTaskByName(queryName: String): LiveData<List<TaskModel>>

    @Query("SELECT id, parentListName FROM TaskModel")
    fun getTaskParentListName(): LiveData<List<TaskListModel>>
}
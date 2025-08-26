package com.mycompany.firstapp.repository

import com.mycompany.firstapp.database.TaskDatabase
import com.mycompany.firstapp.model.TaskListModel
import com.mycompany.firstapp.model.TaskModel

class TaskRepository(val database: TaskDatabase) {

    suspend fun addTask(task: TaskModel) = database.taskDao().addTask(task)

    suspend fun addTaskList(taskList: TaskListModel) = database.taskDao().addTaskList(taskList)

    suspend fun deleteTask(task: TaskModel) = database.taskDao().deleteTask(task)

    suspend fun deleteTaskList(taskList: TaskListModel) = database.taskDao().deleteTaskList(taskList)

    suspend fun updateTask(task: TaskModel) = database.taskDao().updateTask(task)

    suspend fun updateTaskList(taskList: TaskListModel) = database.taskDao().updateTaskListModel(taskList)

    suspend fun updateParentListName(oldName: String, newName: String) = database.taskDao().updateParentListName(oldName, newName)

    suspend fun deleteTaskByParentListName(parentListName: String) = database.taskDao().deleteTaskByParentListName(parentListName)

    fun getDoneTasks(parentListName: String) = database.taskDao().getDoneTasks(parentListName)

    fun getAllTasks(parentListName: String) = database.taskDao().getAllTasks(parentListName)

    fun getAllTaskLists() = database.taskDao().getAllTaskLists()

    fun searchTaskByName(queryName: String) = database.taskDao().searchTaskByName(queryName)

    fun getTaskParentListName() = database.taskDao().getTaskParentListName()

}
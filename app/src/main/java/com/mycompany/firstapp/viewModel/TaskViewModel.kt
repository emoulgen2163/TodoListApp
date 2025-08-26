package com.mycompany.firstapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.mycompany.firstapp.model.TaskListModel
import com.mycompany.firstapp.model.TaskModel
import com.mycompany.firstapp.repository.TaskRepository
import com.mycompany.firstapp.utils.MySingleton

import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository): ViewModel() {

    private val selectedCategory = MutableLiveData(MySingleton.parentList)

    val tasks = selectedCategory.switchMap { category ->
        repository.getAllTasks(category) // returns LiveData from Room

    }

    val doneTasks = selectedCategory.switchMap { category ->
        repository.getDoneTasks(category) // returns LiveData from Room
    }


    fun setCategory(category: String) {
        selectedCategory.value = category
    }

    fun addTask(task: TaskModel){
        viewModelScope.launch{
            repository.addTask(task)
        }
    }

    fun addTaskList(taskList: TaskListModel){
        viewModelScope.launch {
            repository.addTaskList(taskList)
        }
    }

    fun deleteTask(task: TaskModel){
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun deleteTaskList(taskList: TaskListModel){
        viewModelScope.launch {
            repository.deleteTaskList(taskList)
        }
    }

    fun deleteTaskByParentListName(parentListName: String){
        viewModelScope.launch {
            repository.deleteTaskByParentListName(parentListName)
        }
    }

    fun updateTask(task: TaskModel){
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun updateTaskList(taskList: TaskListModel){
        viewModelScope.launch {
            repository.updateTaskList(taskList)
        }
    }

    fun updateParentListName(oldName: String, newName: String){
        viewModelScope.launch {
            repository.updateParentListName(oldName, newName)

        }
    }

    fun getTaskParentListName() = repository.getTaskParentListName()

    fun searchTaskByName(queryName: String) = repository.searchTaskByName(queryName)

    fun getAllTaskLists() = repository.getAllTaskLists()


}
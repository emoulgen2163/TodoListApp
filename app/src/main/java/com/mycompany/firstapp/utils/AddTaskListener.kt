package com.mycompany.firstapp.utils

import com.mycompany.firstapp.model.TaskModel

interface AddTaskListener {

    fun onTaskChecked(task: TaskModel)
}
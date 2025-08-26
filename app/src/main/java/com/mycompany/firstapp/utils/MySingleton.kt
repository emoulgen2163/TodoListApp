package com.mycompany.firstapp.utils

import com.mycompany.firstapp.model.TaskListModel

object MySingleton {

    var parentList = "Default"

    var taskList = mutableListOf<TaskListModel>()
}
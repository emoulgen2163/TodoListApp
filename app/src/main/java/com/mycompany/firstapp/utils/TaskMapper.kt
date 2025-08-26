package com.mycompany.firstapp.utils

import com.mycompany.firstapp.model.TaskListModel
import com.mycompany.firstapp.model.TaskModel

class TaskMapper {

    fun taskListModelToMap(taskListModel: List<TaskListModel>): ArrayList<TaskListModel>{

        val map = hashMapOf<String, Int>()

        val listModel = arrayListOf<TaskListModel>()

        taskListModel.forEach {
            if (map.containsKey(it.parentListName)){
                map[it.parentListName] = map[it.parentListName]!!.plus(1)
            } else{
                map[it.parentListName] = 1
            }

        }

        MySingleton.taskList.forEach {

            if (!map.containsKey(it.parentListName)){
                map[it.parentListName] = 0
            }
        }

        for (key in map.keys){
            val id = MySingleton.taskList.find {
                it.parentListName == key
            }?.id

            val model = TaskListModel(id = id!!, parentListName = key, count = map[key])
            listModel.add(model)
        }


        return listModel
    }
}
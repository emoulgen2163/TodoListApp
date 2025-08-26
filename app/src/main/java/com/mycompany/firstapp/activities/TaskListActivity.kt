package com.mycompany.firstapp.activities

import android.app.AlertDialog
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.util.copy
import com.mycompany.firstapp.R
import com.mycompany.firstapp.adapters.TaskListAdapter
import com.mycompany.firstapp.database.TaskDatabase
import com.mycompany.firstapp.databinding.ActivityTaskListBinding
import com.mycompany.firstapp.model.TaskListModel
import com.mycompany.firstapp.repository.TaskRepository
import com.mycompany.firstapp.utils.MySingleton
import com.mycompany.firstapp.utils.TaskMapper
import com.mycompany.firstapp.viewModel.TaskViewModel
import com.mycompany.firstapp.viewModel.TaskViewModelFactory

class TaskListActivity : AppCompatActivity() {

    lateinit var binding: ActivityTaskListBinding
    lateinit var taskListAdapter: TaskListAdapter
    lateinit var taskViewModel: TaskViewModel
    lateinit var repository: TaskRepository
    lateinit var database: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_list)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Change the color dynamically
        val upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24)
        upArrow?.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
        supportActionBar?.setHomeAsUpIndicator(upArrow)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // or your custom action
        }

        database = TaskDatabase.createDatabase(this)
        repository = TaskRepository(database)

        val viewModelFactory = TaskViewModelFactory(repository)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        taskListAdapter = TaskListAdapter(
            onEditClick = {
                showEditTaskDialog(it.parentListName)
        },
            onDeleteClick = {
                taskViewModel.deleteTaskList(it)
                taskViewModel.deleteTaskByParentListName(it.parentListName)
                updateRecyclerView(it, it.parentListName, 1)
        }
        )

        binding.recyclerView2.apply {
            adapter = taskListAdapter
            layoutManager = LinearLayoutManager(this@TaskListActivity)
        }

        taskViewModel.getTaskParentListName().observe(this){ taskModels ->
            taskModels?.let { listModels ->
                val task = TaskMapper().taskListModelToMap(listModels)
                val sortedList = task.partition{
                    it.parentListName == "Default"
                }.let { (defaultItems, otherItems) ->
                    defaultItems + otherItems
                }
                taskListAdapter.differ.submitList(sortedList)
            }
        }

        taskViewModel.getAllTaskLists().observe(this){ taskListModel ->
            taskListModel?.let { listModel ->
                MySingleton.taskList = listModel as MutableList<TaskListModel>
                taskListAdapter.differ.submitList(listModel)
            }
        }

        binding.addTaskList.setOnClickListener {
            showAddTaskDialog()
        }

    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add List")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Add"){ dialog, _ ->
            val newTaskName = input.text.toString().trim()
            val taskList = TaskListModel(parentListName = newTaskName)
            taskViewModel.addTaskList(taskList)

            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.cancel()
        }


        builder.show()
    }

    private fun showEditTaskDialog(parentListName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit List")

        val input = EditText(this)
        input.setText(parentListName)
        input.inputType = InputType.TYPE_CLASS_TEXT

        builder.setView(input)
        builder.setPositiveButton("Save"){ dialog, _ ->
            val newTaskName = input.text.toString().trim()
            if (newTaskName.isNotEmpty()){
                taskViewModel.updateParentListName(parentListName, newTaskName)
                var modelList: TaskListModel? = null
                MySingleton.taskList.forEach {
                    if (it.parentListName == parentListName){
                        modelList = it
                    }
                }
                MySingleton.taskList.removeIf {
                    it.id == modelList!!.id
                }

                updateRecyclerView(modelList, newTaskName, 0)

                MySingleton.taskList.add(modelList!!)
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun updateRecyclerView(
        modelList: TaskListModel?,
        newTaskName: String,
        actionId: Int
    ) {
        val updatedList = taskListAdapter.differ.currentList.toMutableList()
        val itemToUpdate = updatedList.find {
            it.parentListName == modelList!!.parentListName
        }

        when(actionId){
            0 -> {
                if (itemToUpdate != null) {
                    val updatedItem = itemToUpdate.copy(parentListName = newTaskName)
                    val index = updatedList.indexOf(itemToUpdate)
                    updatedList[index] = updatedItem
                }
                modelList!!.parentListName = newTaskName
                taskViewModel.updateTaskList(modelList)
            }

            1 -> {
                if (itemToUpdate != null){
                    updatedList.removeIf {
                        it.parentListName == modelList!!.parentListName
                    }
                }
            }
        }
        taskListAdapter.differ.submitList(updatedList)

    }


}
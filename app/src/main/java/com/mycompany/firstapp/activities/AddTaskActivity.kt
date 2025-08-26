package com.mycompany.firstapp.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.mycompany.firstapp.R
import com.mycompany.firstapp.database.TaskDatabase
import com.mycompany.firstapp.databinding.ActivityAddTaskBinding
import com.mycompany.firstapp.model.TaskModel
import com.mycompany.firstapp.repository.TaskRepository
import com.mycompany.firstapp.utils.AddTaskListener
import com.mycompany.firstapp.utils.MySingleton
import com.mycompany.firstapp.viewModel.TaskViewModel
import com.mycompany.firstapp.viewModel.TaskViewModelFactory
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar

class AddTaskActivity() : AppCompatActivity() {
    lateinit var binding: ActivityAddTaskBinding
    lateinit var taskViewModel: TaskViewModel
    lateinit var viewModelFactory: TaskViewModelFactory
    lateinit var repository: TaskRepository
    lateinit var database: TaskDatabase

    lateinit var status: String
    var currentTaskModel: TaskModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = TaskDatabase.createDatabase(applicationContext)
        repository = TaskRepository(database)
        viewModelFactory = TaskViewModelFactory(repository)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_task)

        binding.toolbar.overflowIcon?.setTint(Color.WHITE)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Change the color dynamically
        val upArrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24)
        upArrow?.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
        supportActionBar?.setHomeAsUpIndicator(upArrow)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // or your custom action
        }

        val batchMode = intent.getBooleanExtra("add_in_batch_mode", false)

        status = if (batchMode) "on" else "off"

        Toast.makeText(this, "Batch Mode $status!", Toast.LENGTH_SHORT).show()

        val task = intent.getParcelableExtra("taskModel", TaskModel::class.java)

        task?.let { taskModel ->
            currentTaskModel = taskModel

            binding.addTaskEditText.setText(currentTaskModel?.name)
            binding.addTaskDueDate.setText(currentTaskModel?.dueDate)
            binding.addTaskDueTime.setText(currentTaskModel?.dueTime)

            binding.delete.visibility = View.VISIBLE
            binding.delete.setOnClickListener {
                taskViewModel.deleteTask(currentTaskModel!!)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }


        binding.addTaskCalendar.setOnClickListener {
            dateDialog()
        }

        binding.addTaskResetCalendar.setOnClickListener {
            binding.addTaskDueDate.setText("")
        }

        binding.addTaskClock.setOnClickListener {
            timeDialog()

        }

        binding.addTaskResetClock.setOnClickListener {
            binding.addTaskDueTime.setText("")
        }

        binding.saveButton.setOnClickListener {
            if (batchMode){
                saveBatchToDb()
            } else{
                saveToDb()
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun saveToDb(){
        val text = binding.addTaskEditText.text.toString()
        val date = binding.addTaskDueDate.text.toString()
        val time = binding.addTaskDueTime.text.toString()


        if (text.isNotEmpty()){
            val task = if (currentTaskModel == null) {
                TaskModel(name = text, dueDate = date, dueTime = time, parentListName = MySingleton.parentList)
            } else {
                // keep the same id for update
                TaskModel(
                    id = currentTaskModel!!.id,
                    name = text,
                    dueDate = date,
                    dueTime = time,
                    parentListName = MySingleton.parentList
                )
            }

            if (currentTaskModel == null){
                taskViewModel.addTask(task)
            } else{
                taskViewModel.updateTask(task)
            }

        }

    }

    private fun saveBatchToDb(){
        val batchTaskList = binding.addTaskEditText.text.toString().split("\n")
        val date = binding.addTaskDueDate.text.toString()
        val time = binding.addTaskDueTime.text.toString()

        batchTaskList.forEach { text ->
            if (text.isNotEmpty()){
                val task = if (currentTaskModel == null) {
                    TaskModel(name = text, dueDate = date, dueTime = time, parentListName = MySingleton.parentList)
                } else {
                    // keep the same id for update
                    TaskModel(
                        id = currentTaskModel!!.id,
                        name = text,
                        dueDate = date,
                        dueTime = time,
                        parentListName = MySingleton.parentList
                    )
                }

                if (currentTaskModel == null){
                    taskViewModel.addTask(task)
                } else{
                    taskViewModel.updateTask(task)
                }
            }
        }

    }

    private fun dateDialog(){

        val calendar = Calendar.getInstance()

        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(this, {
            _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.addTaskDueDate.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    fun timeDialog(){
        val calendar = Calendar.getInstance()

        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            binding.addTaskDueTime.setText(SimpleDateFormat("HH:mm").format(calendar.time))
        }, hour, minute, true).show()
    }

}
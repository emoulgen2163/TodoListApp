package com.mycompany.firstapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.mycompany.firstapp.R
import com.mycompany.firstapp.database.TaskDatabase
import com.mycompany.firstapp.databinding.ActivityMainBinding
import com.mycompany.firstapp.model.TaskModel
import com.mycompany.firstapp.repository.TaskRepository
import com.mycompany.firstapp.viewModel.TaskViewModel
import com.mycompany.firstapp.viewModel.TaskViewModelFactory
import androidx.core.graphics.toColorInt
import com.mycompany.firstapp.model.TaskListModel
import com.mycompany.firstapp.utils.MySingleton
import androidx.core.content.edit
import androidx.core.net.toUri
import com.mycompany.firstapp.activities.fragments.PagerFragment
import com.mycompany.firstapp.activities.fragments.SearchFragment


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener{
    lateinit var binding: ActivityMainBinding
    lateinit var taskViewModel: TaskViewModel
    lateinit var repository: TaskRepository
    lateinit var database: TaskDatabase

    lateinit var dropdownAdapter: ArrayAdapter<String>

    val list = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        database = TaskDatabase.createDatabase(this)
        repository = TaskRepository(database)
        val viewModelFactory = TaskViewModelFactory(repository)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        binding.toolbar.overflowIcon?.setTint(Color.WHITE)
        setSupportActionBar(binding.toolbar)

        dropdownAdapter = object : ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, list){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).setTextColor(Color.WHITE)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val color = "#014172".toColorInt()
                (view as TextView).apply {
                    setBackgroundColor(color)
                    setTextColor(Color.WHITE)
                } // Dropdown items color
                return view
            }
        }


        binding.dropdown.adapter = dropdownAdapter

        binding.dropdown.onItemSelectedListener = this

        binding.dropdown.viewTreeObserver.addOnGlobalLayoutListener {
            binding.dropdown.dropDownWidth = binding.dropdown.width
        }

        binding.floatingActionButton.setOnClickListener{
            startActivity(Intent(this,AddTaskActivity::class.java))

        }

        isFirstRun()

        taskViewModel.getAllTaskLists().observe(this){ taskListModel ->
            taskListModel?.let { listModel ->
                MySingleton.taskList = listModel as MutableList<TaskListModel>

                list.clear()
                MySingleton.taskList.forEach {
                    list.add(it.parentListName)
                }

                dropdownAdapter.notifyDataSetChanged()
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.container, PagerFragment()).commit()

        binding.tvTaskId.setOnClickListener {
            val text = binding.editTextTaskId.text.toString()
            val task = TaskModel(name = text, parentListName = MySingleton.parentList)
            taskViewModel.addTask(task)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_bar, menu)
        menuInflater.inflate(R.menu.custom_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.search -> {

                searchView()
                true
            }

            R.id.taskList -> {
                val intent = Intent(this, TaskListActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.addInBatchMode -> {
                val intent = Intent(this, AddTaskActivity::class.java)
                intent.putExtra("add_in_batch_mode", true)
                startActivity(intent)
                true
            }

            R.id.moreApps -> {
                val developerName = "Splend Apps"
                var url: String
                try {
                    url = "market://search?q=pub:$developerName"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    intent.setPackage("com.android.vending")
                    startActivity(intent)
                } catch (e: Exception) {
                    url = "https://play.google.com/store/search?q=pub:$developerName"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    startActivity(intent)
                }

                true
            }

            R.id.sendFeedback -> {

                val recipient = "emirhanulgen34@gmail.com"
                val subject = "Send Feedback"
                val body = "Type your complaint..."

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:".toUri()
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                }

                if (intent.resolveActivity(packageManager) != null){
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
                }

                true
            }

            R.id.followUs -> {

                val url = "https://www.instagram.com/"
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                startActivity(intent)

                true
            }

            R.id.invite -> {

                val text = "Check out my app!"
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                val chooser = Intent.createChooser(intent, "Share via")
                startActivity(chooser)

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun searchView() {
        binding.containerSearch.visibility = View.VISIBLE
        val fragment = SearchFragment()
        supportFragmentManager.beginTransaction().replace(R.id.containerSearch, fragment).commit()
        binding.container.visibility = View.INVISIBLE

        binding.apply {
            image.visibility = View.GONE
            dropdown.visibility = View.GONE
            backButton.visibility = View.VISIBLE
            searchEditText.visibility = View.VISIBLE
        }

        binding.backButton.setOnClickListener {
            binding.containerSearch.visibility = View.INVISIBLE
            binding.container.visibility = View.VISIBLE

            binding.apply {
                backButton.visibility = View.GONE
                searchEditText.visibility = View.GONE
                image.visibility = View.VISIBLE
                dropdown.visibility = View.VISIBLE
            }
        }


    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val category = parent.getItemAtPosition(pos) as String
        taskViewModel.setCategory(category)
        MySingleton.parentList = category
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    fun isFirstRun(){
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("isFirstRun", true)

        if (isFirstRun){
            val taskList = listOf("Default", "Shopping List", "Personal", "Work", "Wishlist")

            taskList.forEach {
                taskViewModel.addTaskList(TaskListModel(parentListName = it))
            }

            prefs.edit {
                putBoolean("isFirstRun", false)
            }
        }
    }
}
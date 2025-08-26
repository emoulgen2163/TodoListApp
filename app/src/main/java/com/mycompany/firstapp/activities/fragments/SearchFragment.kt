package com.mycompany.firstapp.activities.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.mycompany.firstapp.activities.MainActivity
import com.mycompany.firstapp.adapters.TaskAdapter
import com.mycompany.firstapp.databinding.FragmentSearchBinding
import com.mycompany.firstapp.model.TaskModel
import com.mycompany.firstapp.utils.AddTaskListener
import com.mycompany.firstapp.viewModel.TaskViewModel

class SearchFragment : Fragment(), AddTaskListener, SearchView.OnQueryTextListener{

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    lateinit var taskViewModel: TaskViewModel
    lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        taskAdapter = TaskAdapter(context = requireContext() as MainActivity, addTaskListener = this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext() as MainActivity)
            adapter = taskAdapter
        }

        taskViewModel = (activity as MainActivity).taskViewModel

        (activity as MainActivity).binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                searchTask(p0.toString())
            }

        })

        return binding.root
    }


    override fun onTaskChecked(task: TaskModel) {
        task.isTaskDone = true
        taskViewModel.updateTask(task)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchTask(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            searchTask(newText)
        }
        return true
    }

    private fun searchTask(query: String?) {
        val searchQuery = "$query"
        taskViewModel.searchTaskByName(searchQuery).observe(viewLifecycleOwner){
            taskAdapter.setUpList(it)
        }
    }

}
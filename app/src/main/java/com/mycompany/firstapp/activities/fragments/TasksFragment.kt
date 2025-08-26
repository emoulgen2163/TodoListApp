package com.mycompany.firstapp.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mycompany.firstapp.activities.MainActivity
import com.mycompany.firstapp.adapters.TaskAdapter
import com.mycompany.firstapp.databinding.FragmentTasksBinding
import com.mycompany.firstapp.model.TaskModel
import com.mycompany.firstapp.utils.AddTaskListener
import com.mycompany.firstapp.viewModel.TaskViewModel

class TasksFragment : Fragment(), AddTaskListener {

    private lateinit var _binding: FragmentTasksBinding
    private val binding get() = _binding
    lateinit var taskViewModel: TaskViewModel
    lateinit var taskAdapter: TaskAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTasksBinding.inflate(inflater, container, false)

        taskAdapter = TaskAdapter(context = requireContext() as MainActivity, addTaskListener = this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext() as MainActivity)
            adapter = taskAdapter
        }

        taskViewModel = (activity as MainActivity).taskViewModel


        taskViewModel.tasks.observe(viewLifecycleOwner){
            taskAdapter.setUpList(it)
        }


        return binding.root
    }

    override fun onTaskChecked(task: TaskModel) {
        task.isTaskDone = true
        taskViewModel.updateTask(task)
    }

}
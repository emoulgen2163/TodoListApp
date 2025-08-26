package com.mycompany.firstapp.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mycompany.firstapp.activities.MainActivity
import com.mycompany.firstapp.adapters.TaskAdapter
import com.mycompany.firstapp.databinding.FragmentDoneTasksBinding
import com.mycompany.firstapp.model.TaskModel
import com.mycompany.firstapp.utils.AddTaskListener
import com.mycompany.firstapp.viewModel.TaskViewModel


class DoneTasksFragment : Fragment(), AddTaskListener {

    private lateinit var _binding: FragmentDoneTasksBinding
    private val binding get() = _binding
    lateinit var taskViewModel: TaskViewModel
    lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDoneTasksBinding.inflate(inflater, container, false)

        taskAdapter = TaskAdapter(requireContext() as MainActivity, this)

        binding.recyclerView.adapter = taskAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext() as MainActivity)


        taskViewModel = (activity as MainActivity).taskViewModel

        taskViewModel.doneTasks.observe(viewLifecycleOwner){
            taskAdapter.setUpList(it)
        }

        return binding.root
    }

    override fun onTaskChecked(task: TaskModel) {
        task.isTaskDone = false
        taskViewModel.updateTask(task)
    }


}
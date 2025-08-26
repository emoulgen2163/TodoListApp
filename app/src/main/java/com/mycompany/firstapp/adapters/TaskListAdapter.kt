package com.mycompany.firstapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mycompany.firstapp.databinding.ActivityTaskListBinding
import com.mycompany.firstapp.databinding.TaskItemBinding
import com.mycompany.firstapp.model.TaskListModel

class TaskListAdapter(val onEditClick: (TaskListModel) -> Unit, val onDeleteClick: (TaskListModel) -> Unit): RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<TaskListModel>(){
        override fun areItemsTheSame(
            oldItem: TaskListModel,
            newItem: TaskListModel
        ): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TaskListModel,
            newItem: TaskListModel
        ): Boolean {
            return oldItem == newItem
        }


    }

    val differ = AsyncListDiffer(this, differCallback)

    class TaskListViewHolder(binding: TaskItemBinding): RecyclerView.ViewHolder(binding.root){
        val taskName = binding.taskName
        val taskDescription = binding.taskDescription
        val edit = binding.edit
        val delete = binding.delete
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskListViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TaskListViewHolder,
        position: Int
    ) {
        val task = differ.currentList[position]

        holder.taskName.text = task.parentListName
        holder.taskDescription.text = "${task.count} tasks"

        if (task.parentListName == "Default"){
            holder.edit.visibility = View.INVISIBLE
            holder.delete.visibility = View.INVISIBLE
        }

        holder.edit.setOnClickListener {
            onEditClick(task)
        }

        holder.delete.setOnClickListener {
            onDeleteClick(task)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }



}
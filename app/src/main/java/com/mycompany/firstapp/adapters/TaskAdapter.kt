package com.mycompany.firstapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mycompany.firstapp.activities.AddTaskActivity
import com.mycompany.firstapp.databinding.RecViewItemBinding
import com.mycompany.firstapp.model.TaskModel
import com.mycompany.firstapp.utils.AddTaskListener

class TaskAdapter(val context: Context, val addTaskListener: AddTaskListener): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var items: ArrayList<TaskModel> = ArrayList()

    class TaskViewHolder(binding: RecViewItemBinding): RecyclerView.ViewHolder(binding.root){
        val linearLayoutItemId = binding.linearLayoutItemId
        val taskTv = binding.text
        val checkbox = binding.checkbox

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {
        val binding = RecViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = items[position]
        holder.taskTv.text = task.name

        holder.linearLayoutItemId.setOnClickListener {
            val intent = Intent(context, AddTaskActivity::class.java)
            intent.putExtra("taskModel", task)
            context.startActivity(intent)
        }

        holder.checkbox.isChecked = task.isTaskDone

        holder.checkbox.setOnCheckedChangeListener{ _, isChecked ->
            addTaskListener.onTaskChecked(task)
        }
    }

    override fun getItemCount(): Int = items.size

    fun setUpList(tasks: List<TaskModel>){
        items = ArrayList(tasks)
        notifyDataSetChanged()
    }

}
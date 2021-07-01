package com.codinginflow.mvvmtodo.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.databinding.ItemTaskBinding

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDifference()) {

    class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(task:Task){
            binding.apply {
                checkboxCompleted.isChecked=task.completed
                textTaskTitle.text=task.name
                textTaskTitle.paint.isStrikeThruText=task.completed
                imgPriority.isVisible = task.priority
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val viewHolder= ItemTaskBinding.inflate(LayoutInflater.from(parent.context))
        return TaskViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TaskDifference : DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return  oldItem==newItem
        }

    }
}
package com.example.app1.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.app1.databinding.ItemTaskCardBinding
import com.example.app1.models.Task

class TasksAdapter(
    private var tasks: List<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onTaskComplete: ((Task, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {
    
    fun updateTasks(newTasks: List<Task>) {
        try {
            Log.d("TasksAdapter", "Updating tasks: ${newTasks.size} items")
            val diffResult = DiffUtil.calculateDiff(TasksDiffCallback(tasks, newTasks))
            tasks = newTasks
            diffResult.dispatchUpdatesTo(this)
        } catch (e: Exception) {
            Log.e("TasksAdapter", "Error updating tasks, falling back to notifyDataSetChanged", e)
            tasks = newTasks
            notifyDataSetChanged()
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }
    
    override fun getItemCount(): Int = tasks.size
    
    inner class TaskViewHolder(
        private val binding: ItemTaskCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(task: Task) {
            binding.textTaskTitle.text = task.title
            binding.textTaskDescription.text = if (task.description.isNotEmpty()) task.description else "No description"
            
            // Set priority color
            val priorityColor = when (task.priority) {
                Task.Priority.HIGH -> android.graphics.Color.parseColor("#FF5722")
                Task.Priority.MEDIUM -> android.graphics.Color.parseColor("#FF9800")
                Task.Priority.LOW -> android.graphics.Color.parseColor("#4CAF50")
            }
            binding.viewPriorityIndicator.setBackgroundColor(priorityColor)
            
            // Set category
            binding.textTaskCategory.text = task.category.name.lowercase().replaceFirstChar { it.uppercase() }
            
            // Avoid triggering listener while updating checked state
            binding.checkboxTask.setOnCheckedChangeListener(null)
            binding.checkboxTask.isChecked = task.isCompleted
            
            // Handle click
            binding.root.setOnClickListener {
                onTaskClick(task)
            }
            
            binding.checkboxTask.setOnCheckedChangeListener { _, isChecked ->
                // Defer to next loop to avoid notifyDataSetChanged during layout
                binding.root.post {
                    onTaskComplete?.invoke(task, isChecked)
                }
            }
        }
    }
}

class TasksDiffCallback(private val oldList: List<Task>, private val newList: List<Task>) : DiffUtil.Callback() {
    
    override fun getOldListSize(): Int = oldList.size
    
    override fun getNewListSize(): Int = newList.size
    
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }
    
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

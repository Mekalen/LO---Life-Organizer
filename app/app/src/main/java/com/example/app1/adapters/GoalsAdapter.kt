package com.example.app1.adapters

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app1.databinding.ItemGoalCardBinding
import com.example.app1.models.Goal

class GoalsAdapter(
    private var goals: List<Goal>,
    private val onGoalClick: (Goal) -> Unit
) : RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {
    
    fun updateGoals(newGoals: List<Goal>) {
        try {
            Log.d("GoalsAdapter", "Updating goals: ${newGoals.size} items")
            goals = newGoals
            notifyDataSetChanged()
        } catch (e: Exception) {
            Log.e("GoalsAdapter", "Error updating goals", e)
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoalViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(goals[position])
    }
    
    override fun getItemCount(): Int = goals.size
    
    inner class GoalViewHolder(
        private val binding: ItemGoalCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(goal: Goal) {
            binding.textGoalTitle.text = goal.title
            binding.textGoalDescription.text = goal.description
            binding.progressBarGoal.progress = goal.progress
            binding.textGoalProgress.text = "${goal.progress}%"
            
            // Set progress color based on completion
            val progressColor = when {
                goal.progress >= 80 -> android.graphics.Color.parseColor("#4CAF50")
                goal.progress >= 50 -> android.graphics.Color.parseColor("#FFD700")
                else -> android.graphics.Color.parseColor("#FF0000")
            }
            binding.progressBarGoal.progressTintList = ColorStateList.valueOf(progressColor)
            
            // Show deadline if set
            goal.deadline?.let { deadline ->
                val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                binding.textGoalDeadline.text = "Deadline: ${dateFormat.format(deadline)}"
                binding.textGoalDeadline.visibility = android.view.View.VISIBLE
            } ?: run {
                binding.textGoalDeadline.visibility = android.view.View.GONE
            }
            
            // Handle click
            binding.root.setOnClickListener {
                onGoalClick(goal)
            }
            
            binding.buttonUpdateProgress.setOnClickListener {
                // Handle progress update
            }
        }
    }
}

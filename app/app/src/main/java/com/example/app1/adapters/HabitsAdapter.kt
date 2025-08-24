package com.example.app1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app1.databinding.ItemHabitCardBinding
import com.example.app1.models.Habit

class HabitsAdapter(
    private var habits: List<Habit>,
    private val onHabitClick: (Habit) -> Unit,
    private val onHabitTrack: (Habit) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {
    
    fun updateHabits(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }
    
    override fun getItemCount(): Int = habits.size
    
    inner class HabitViewHolder(
        private val binding: ItemHabitCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(habit: Habit) {
            binding.textHabitName.text = habit.name
            binding.textHabitFrequency.text = habit.frequency.name.lowercase().replaceFirstChar { it.uppercase() }
            binding.textCurrentStreak.text = "${habit.currentStreak} days"
            binding.textLongestStreak.text = "Longest: ${habit.longestStreak} days"
            binding.textTotalCompletions.text = "${habit.totalCompletions} times"
            
            // Handle click
            binding.root.setOnClickListener {
                onHabitClick(habit)
            }
            
            // Handle track button
            binding.buttonTrackHabit.setOnClickListener {
                onHabitTrack(habit)
            }
        }
    }
}

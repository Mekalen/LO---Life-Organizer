package com.example.app1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app1.adapters.TasksAdapter
import com.example.app1.adapters.HabitsAdapter
import com.example.app1.adapters.GoalsAdapter
import com.example.app1.data.DataManager
import com.example.app1.databinding.FragmentDashboardBinding
import com.example.app1.models.Task
import com.example.app1.models.Habit
import com.example.app1.models.Goal
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var habitsAdapter: HabitsAdapter
    private lateinit var goalsAdapter: GoalsAdapter
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupRecyclerViews()
        setupClickListeners()
        loadDashboardData()
        updateMoodDisplay()
    }
    
    private fun setupRecyclerViews() {
        // Setup Tasks RecyclerView
        tasksAdapter = TasksAdapter(
            emptyList(),
            onTaskClick = { task ->
                // Navigate to tasks fragment or show task details
            },
            onTaskComplete = { task, isCompleted ->
                val updatedTask = task.copy(isCompleted = isCompleted)
                dataManager.updateTask(updatedTask)
                loadDashboardData()
            }
        )
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = tasksAdapter
        }
        
        // Setup Habits RecyclerView
        habitsAdapter = HabitsAdapter(
            emptyList(),
            onHabitClick = { habit ->
                // Navigate to habits fragment or show habit details
            },
            onHabitTrack = { habit ->
                dataManager.trackHabit(habit.id)
                loadDashboardData()
            }
        )
        binding.recyclerViewHabits.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = habitsAdapter
        }
        
        // Setup Goals RecyclerView
        goalsAdapter = GoalsAdapter(emptyList()) { goal ->
            // Navigate to goals fragment or show goal details
        }
        binding.recyclerViewGoals.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = goalsAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddTask.setOnClickListener {
            // Navigate to add task
        }
        
        binding.fabAddHabit.setOnClickListener {
            // Navigate to add habit
        }
        
        binding.fabAddGoal.setOnClickListener {
            // Navigate to add goal
        }
        
        binding.cardMood.setOnClickListener {
            showMoodDialog()
        }
    }
    
    private fun loadDashboardData() {
        val tasks = dataManager.getTasks()
        val habits = dataManager.getHabits()
        val goals = dataManager.getGoals()
        
        // Show only today's tasks (for simplicity, show all tasks)
        val todayTasks = tasks.take(3) // Show first 3 tasks
        tasksAdapter.updateTasks(todayTasks)
        
        // Show habits with current streaks
        val activeHabits = habits.sortedByDescending { it.currentStreak }.take(3)
        habitsAdapter.updateHabits(activeHabits)
        
        // Show goals with progress
        val activeGoals = goals.sortedByDescending { it.progress }.take(3)
        goalsAdapter.updateGoals(activeGoals)
        
        updateStats(tasks, habits, goals)
    }
    
    private fun updateStats(tasks: List<Task>, habits: List<Habit>, goals: List<Goal>) {
        val completedTasks = tasks.count { it.isCompleted }
        val totalTasks = tasks.size
        val averageHabitStreak = if (habits.isNotEmpty()) habits.map { it.currentStreak }.average().toInt() else 0
        val averageGoalProgress = if (goals.isNotEmpty()) goals.map { it.progress }.average().toInt() else 0
        
        binding.textTasksProgress.text = "$completedTasks/$totalTasks completed"
        binding.textHabitStreak.text = "Avg: $averageHabitStreak days"
        binding.textGoalProgress.text = "Avg: $averageGoalProgress%"
    }
    
    private fun updateMoodDisplay() {
        val currentMood = dataManager.getCurrentMood()
        if (currentMood != null) {
            val (mood, date) = currentMood
            binding.textMoodStatus.text = "Your mood: $mood"
            binding.textMoodDate.text = "Set on ${dateFormat.format(date)}"
        } else {
            binding.textMoodStatus.text = "Tap to set your mood"
            binding.textMoodDate.text = "Not set yet"
        }
    }
    
    private fun showMoodDialog() {
        val moods = arrayOf("😊 Happy", "😌 Calm", "😤 Stressed", "😴 Tired", "😃 Excited", "😔 Sad", "😡 Angry", "😐 Neutral")
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("How are you feeling today?")
            .setItems(moods) { _, which ->
                val selectedMood = moods[which].substringAfter(" ")
                dataManager.saveMood(selectedMood)
                updateMoodDisplay()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

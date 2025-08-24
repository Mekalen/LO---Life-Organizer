package com.example.app1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
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
        Log.d("DashboardFragment", "onViewCreated() called")
        
        try {
            dataManager = DataManager(requireContext())
            Log.d("DashboardFragment", "DataManager initialized")
            
            setupRecyclerViews()
            Log.d("DashboardFragment", "RecyclerViews setup completed")
            
            setupClickListeners()
            Log.d("DashboardFragment", "Click listeners setup completed")
            
            loadDashboardData()
            Log.d("DashboardFragment", "Dashboard data loaded")
            
            updateMoodDisplay()
            Log.d("DashboardFragment", "Mood display updated")
            
            // Update toolbar title
            (requireActivity() as MainActivity).updateToolbarTitle("Dashboard")
            Log.d("DashboardFragment", "Toolbar title updated")
            
            Log.d("DashboardFragment", "onViewCreated() completed successfully")
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error in onViewCreated()", e)
        }
    }
    
    private fun setupRecyclerViews() {
        // Setup Tasks RecyclerView
        tasksAdapter = TasksAdapter(
            emptyList(),
            onTaskClick = { task ->
                showEditTaskDialog(task)
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
                showEditHabitDialog(habit)
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
            showEditGoalDialog(goal)
        }
        binding.recyclerViewGoals.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = goalsAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.buttonAddTaskFromDashboard.setOnClickListener {
            showAddTaskDialog()
        }
        
        binding.buttonAddHabitFromDashboard.setOnClickListener {
            showAddHabitDialog()
        }
        
        binding.buttonAddGoalFromDashboard.setOnClickListener {
            showAddGoalDialog()
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
    
    private fun showAddTaskDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_add_task, null)
        
        // Setup priority spinner
        val priorityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Task.Priority.values().map { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } }
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val prioritySpinner = dialogBinding.findViewById<Spinner>(R.id.spinnerPriority)
        prioritySpinner.adapter = priorityAdapter
        
        // Setup category spinner
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Task.Category.values().map { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } }
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val categorySpinner = dialogBinding.findViewById<Spinner>(R.id.spinnerCategory)
        categorySpinner.adapter = categoryAdapter
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Task")
            .setView(dialogBinding)
            .setPositiveButton("Add") { _, _ ->
                val title = dialogBinding.findViewById<EditText>(R.id.editTextTitle).text.toString()
                val description = dialogBinding.findViewById<EditText>(R.id.editTextDescription).text.toString()
                val priority = Task.Priority.values()[prioritySpinner.selectedItemPosition]
                val category = Task.Category.values()[categorySpinner.selectedItemPosition]
                
                if (title.isNotEmpty()) {
                    val task = Task(
                        title = title,
                        description = description,
                        priority = priority,
                        category = category
                    )
                    dataManager.addTask(task)
                    loadDashboardData()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showEditTaskDialog(task: Task) {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_add_task, null)
        
        // Pre-fill the fields
        dialogBinding.findViewById<EditText>(R.id.editTextTitle).setText(task.title)
        dialogBinding.findViewById<EditText>(R.id.editTextDescription).setText(task.description)
        
        // Setup spinners
        val priorityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Task.Priority.values().map { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } }
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val prioritySpinner = dialogBinding.findViewById<Spinner>(R.id.spinnerPriority)
        prioritySpinner.adapter = priorityAdapter
        prioritySpinner.setSelection(task.priority.ordinal)
        
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Task.Category.values().map { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } }
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val categorySpinner = dialogBinding.findViewById<Spinner>(R.id.spinnerCategory)
        categorySpinner.adapter = categoryAdapter
        categorySpinner.setSelection(task.category.ordinal)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Task")
            .setView(dialogBinding)
            .setPositiveButton("Update") { _, _ ->
                val title = dialogBinding.findViewById<EditText>(R.id.editTextTitle).text.toString()
                val description = dialogBinding.findViewById<EditText>(R.id.editTextDescription).text.toString()
                val priority = Task.Priority.values()[prioritySpinner.selectedItemPosition]
                val category = Task.Category.values()[categorySpinner.selectedItemPosition]
                
                if (title.isNotEmpty()) {
                    val updatedTask = task.copy(
                        title = title,
                        description = description,
                        priority = priority,
                        category = category
                    )
                    dataManager.updateTask(updatedTask)
                    loadDashboardData()
                }
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete") { _, _ ->
                showDeleteTaskConfirmation(task)
            }
            .show()
    }
    
    private fun showDeleteTaskConfirmation(task: Task) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete '${task.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                dataManager.deleteTask(task.id)
                loadDashboardData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showAddHabitDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        
        // Setup frequency spinner
        val frequencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Habit.Frequency.values().map { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } }
        )
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val frequencySpinner = dialogBinding.findViewById<Spinner>(R.id.spinnerFrequency)
        frequencySpinner.adapter = frequencyAdapter
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Habit")
            .setView(dialogBinding)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.findViewById<EditText>(R.id.editTextHabitName).text.toString()
                val frequency = Habit.Frequency.values()[frequencySpinner.selectedItemPosition]
                
                if (name.isNotEmpty()) {
                    val habit = Habit(
                        name = name,
                        frequency = frequency
                    )
                    dataManager.addHabit(habit)
                    loadDashboardData()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showEditHabitDialog(habit: Habit) {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        
        // Pre-fill the fields
        dialogBinding.findViewById<EditText>(R.id.editTextHabitName).setText(habit.name)
        
        // Setup frequency spinner
        val frequencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Habit.Frequency.values().map { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } }
        )
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val frequencySpinner = dialogBinding.findViewById<Spinner>(R.id.spinnerFrequency)
        frequencySpinner.adapter = frequencyAdapter
        frequencySpinner.setSelection(habit.frequency.ordinal)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Habit")
            .setView(dialogBinding)
            .setPositiveButton("Update") { _, _ ->
                val name = dialogBinding.findViewById<EditText>(R.id.editTextHabitName).text.toString()
                val frequency = Habit.Frequency.values()[frequencySpinner.selectedItemPosition]
                
                if (name.isNotEmpty()) {
                    val updatedHabit = habit.copy(
                        name = name,
                        frequency = frequency
                    )
                    dataManager.updateHabit(updatedHabit)
                    loadDashboardData()
                }
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete") { _, _ ->
                showDeleteHabitConfirmation(habit)
            }
            .show()
    }
    
    private fun showDeleteHabitConfirmation(habit: Habit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete '${habit.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                dataManager.deleteHabit(habit.id)
                loadDashboardData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showAddGoalDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_add_goal, null)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Goal")
            .setView(dialogBinding)
            .setPositiveButton("Add") { _, _ ->
                val title = dialogBinding.findViewById<EditText>(R.id.editTextGoalTitle).text.toString()
                val description = dialogBinding.findViewById<EditText>(R.id.editTextGoalDescription).text.toString()
                val targetValue = dialogBinding.findViewById<EditText>(R.id.editTextTargetValue).text.toString()
                val currentValue = dialogBinding.findViewById<EditText>(R.id.editTextCurrentValue).text.toString()
                
                if (title.isNotEmpty()) {
                    val goal = Goal(
                        title = title,
                        description = description,
                        targetValue = targetValue,
                        currentValue = currentValue,
                        progress = calculateProgress(currentValue, targetValue)
                    )
                    dataManager.addGoal(goal)
                    loadDashboardData()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showEditGoalDialog(goal: Goal) {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_add_goal, null)
        
        // Pre-fill the fields
        dialogBinding.findViewById<EditText>(R.id.editTextGoalTitle).setText(goal.title)
        dialogBinding.findViewById<EditText>(R.id.editTextGoalDescription).setText(goal.description)
        dialogBinding.findViewById<EditText>(R.id.editTextTargetValue).setText(goal.targetValue)
        dialogBinding.findViewById<EditText>(R.id.editTextCurrentValue).setText(goal.currentValue)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Goal")
            .setView(dialogBinding)
            .setPositiveButton("Update") { _, _ ->
                val title = dialogBinding.findViewById<EditText>(R.id.editTextGoalTitle).text.toString()
                val description = dialogBinding.findViewById<EditText>(R.id.editTextGoalDescription).text.toString()
                val targetValue = dialogBinding.findViewById<EditText>(R.id.editTextTargetValue).text.toString()
                val currentValue = dialogBinding.findViewById<EditText>(R.id.editTextCurrentValue).text.toString()
                
                if (title.isNotEmpty()) {
                    val updatedGoal = goal.copy(
                        title = title,
                        description = description,
                        targetValue = targetValue,
                        currentValue = currentValue,
                        progress = calculateProgress(currentValue, targetValue)
                    )
                    dataManager.updateGoal(updatedGoal)
                    loadDashboardData()
                }
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete") { _, _ ->
                showDeleteGoalConfirmation(goal)
            }
            .show()
    }
    
    private fun showDeleteGoalConfirmation(goal: Goal) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Goal")
            .setMessage("Are you sure you want to delete '${goal.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                dataManager.deleteGoal(goal.id)
                loadDashboardData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun calculateProgress(currentValue: String, targetValue: String): Int {
        return try {
            val current = currentValue.toDoubleOrNull() ?: 0.0
            val target = targetValue.toDoubleOrNull() ?: 1.0
            if (target > 0) {
                ((current / target) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }
        } catch (e: Exception) {
            0
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

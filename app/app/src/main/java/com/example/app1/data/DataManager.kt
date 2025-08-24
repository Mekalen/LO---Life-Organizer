package com.example.app1.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.app1.models.Goal
import com.example.app1.models.Habit
import com.example.app1.models.Task
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.util.*

class DataManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_TASKS = "tasks"
        private const val KEY_HABITS = "habits"
        private const val KEY_GOALS = "goals"
        private const val KEY_MOOD = "current_mood"
        private const val KEY_MOOD_DATE = "mood_date"
    }
    
    // Tasks
    fun saveTasks(tasks: List<Task>) {
        try {
            val json = gson.toJson(tasks)
            sharedPreferences.edit().putString(KEY_TASKS, json).apply()
            Log.d("DataManager", "Tasks saved successfully: ${tasks.size} items")
        } catch (e: Exception) {
            Log.e("DataManager", "Error saving tasks", e)
        }
    }
    
    fun getTasks(): List<Task> {
        return try {
            val json = sharedPreferences.getString(KEY_TASKS, "[]")
            val type = object : TypeToken<List<Task>>() {}.type
            val tasks = gson.fromJson<List<Task>>(json, type) ?: emptyList()
            Log.d("DataManager", "Tasks loaded successfully: ${tasks.size} items")
            tasks
        } catch (e: JsonSyntaxException) {
            Log.e("DataManager", "Error parsing tasks JSON, returning empty list", e)
            emptyList()
        } catch (e: Exception) {
            Log.e("DataManager", "Error loading tasks, returning empty list", e)
            emptyList()
        }
    }
    
    fun addTask(task: Task) {
        val tasks = getTasks().toMutableList()
        val newTask = task.copy(id = UUID.randomUUID().toString())
        tasks.add(newTask)
        saveTasks(tasks)
    }
    
    fun updateTask(task: Task) {
        val tasks = getTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            saveTasks(tasks)
        }
    }
    
    fun deleteTask(taskId: String) {
        val tasks = getTasks().toMutableList()
        tasks.removeAll { it.id == taskId }
        saveTasks(tasks)
    }
    
    // Habits
    fun saveHabits(habits: List<Habit>) {
        try {
            val json = gson.toJson(habits)
            sharedPreferences.edit().putString(KEY_HABITS, json).apply()
            Log.d("DataManager", "Habits saved successfully: ${habits.size} items")
        } catch (e: Exception) {
            Log.e("DataManager", "Error saving habits", e)
        }
    }
    
    fun getHabits(): List<Habit> {
        return try {
            val json = sharedPreferences.getString(KEY_HABITS, "[]")
            val type = object : TypeToken<List<Habit>>() {}.type
            val habits = gson.fromJson<List<Habit>>(json, type) ?: emptyList()
            Log.d("DataManager", "Habits loaded successfully: ${habits.size} items")
            habits
        } catch (e: JsonSyntaxException) {
            Log.e("DataManager", "Error parsing habits JSON, returning empty list", e)
            emptyList()
        } catch (e: Exception) {
            Log.e("DataManager", "Error loading habits, returning empty list", e)
            emptyList()
        }
    }
    
    fun addHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        val newHabit = habit.copy(id = UUID.randomUUID().toString())
        habits.add(newHabit)
        saveHabits(habits)
    }
    
    fun updateHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            saveHabits(habits)
        }
    }
    
    fun deleteHabit(habitId: String) {
        val habits = getHabits().toMutableList()
        habits.removeAll { it.id == habitId }
        saveHabits(habits)
    }
    
    fun trackHabit(habitId: String) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habitId }
        if (index != -1) {
            val habit = habits[index]
            val today = Date()
            val lastTracked = habit.lastTracked
            
            // Check if this is a consecutive day
            val isConsecutive = lastTracked?.let { 
                val diffInMillis = today.time - it.time
                val diffInDays = diffInMillis / (24 * 60 * 60 * 1000)
                diffInDays <= 1
            } ?: true
            
            val newStreak = if (isConsecutive) habit.currentStreak + 1 else 1
            val newLongestStreak = maxOf(newStreak, habit.longestStreak)
            
            val updatedHabit = habit.copy(
                currentStreak = newStreak,
                longestStreak = newLongestStreak,
                totalCompletions = habit.totalCompletions + 1,
                lastTracked = today
            )
            
            habits[index] = updatedHabit
            saveHabits(habits)
        }
    }
    
    // Goals
    fun saveGoals(goals: List<Goal>) {
        try {
            val json = gson.toJson(goals)
            sharedPreferences.edit().putString(KEY_GOALS, json).apply()
            Log.d("DataManager", "Goals saved successfully: ${goals.size} items")
        } catch (e: Exception) {
            Log.e("DataManager", "Error saving goals", e)
        }
    }
    
    fun getGoals(): List<Goal> {
        return try {
            val json = sharedPreferences.getString(KEY_GOALS, "[]")
            val type = object : TypeToken<List<Goal>>() {}.type
            val goals = gson.fromJson<List<Goal>>(json, type) ?: emptyList()
            Log.d("DataManager", "Goals loaded successfully: ${goals.size} items")
            goals
        } catch (e: JsonSyntaxException) {
            Log.e("DataManager", "Error parsing goals JSON, returning empty list", e)
            emptyList()
        } catch (e: Exception) {
            Log.e("DataManager", "Error loading goals, returning empty list", e)
            emptyList()
        }
    }
    
    fun addGoal(goal: Goal) {
        val goals = getGoals().toMutableList()
        val newGoal = goal.copy(id = UUID.randomUUID().toString())
        goals.add(newGoal)
        saveGoals(goals)
    }
    
    fun updateGoal(goal: Goal) {
        val goals = getGoals().toMutableList()
        val index = goals.indexOfFirst { it.id == goal.id }
        if (index != -1) {
            goals[index] = goal
            saveGoals(goals)
        }
    }
    
    fun deleteGoal(goalId: String) {
        val goals = getGoals().toMutableList()
        goals.removeAll { it.id == goalId }
        saveGoals(goals)
    }
    
    // Mood tracking
    fun saveMood(mood: String) {
        try {
            sharedPreferences.edit()
                .putString(KEY_MOOD, mood)
                .putString(KEY_MOOD_DATE, gson.toJson(Date()))
                .apply()
            Log.d("DataManager", "Mood saved successfully: $mood")
        } catch (e: Exception) {
            Log.e("DataManager", "Error saving mood", e)
        }
    }
    
    fun getCurrentMood(): Pair<String, Date>? {
        return try {
            val mood = sharedPreferences.getString(KEY_MOOD, null)
            val dateJson = sharedPreferences.getString(KEY_MOOD_DATE, null)
            
            if (mood != null && dateJson != null) {
                val date = gson.fromJson(dateJson, Date::class.java)
                Log.d("DataManager", "Mood loaded successfully: $mood")
                Pair(mood, date)
            } else {
                Log.d("DataManager", "No mood data found")
                null
            }
        } catch (e: JsonSyntaxException) {
            Log.e("DataManager", "Error parsing mood date JSON", e)
            null
        } catch (e: Exception) {
            Log.e("DataManager", "Error loading mood", e)
            null
        }
    }
}

package com.example.app1.data

import android.content.Context
import android.content.SharedPreferences
import com.example.app1.models.Goal
import com.example.app1.models.Habit
import com.example.app1.models.Task
import com.google.gson.Gson
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
        val json = gson.toJson(tasks)
        sharedPreferences.edit().putString(KEY_TASKS, json).apply()
    }
    
    fun getTasks(): List<Task> {
        val json = sharedPreferences.getString(KEY_TASKS, "[]")
        val type = object : TypeToken<List<Task>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
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
        val json = gson.toJson(habits)
        sharedPreferences.edit().putString(KEY_HABITS, json).apply()
    }
    
    fun getHabits(): List<Habit> {
        val json = sharedPreferences.getString(KEY_HABITS, "[]")
        val type = object : TypeToken<List<Habit>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
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
        val json = gson.toJson(goals)
        sharedPreferences.edit().putString(KEY_GOALS, json).apply()
    }
    
    fun getGoals(): List<Goal> {
        val json = sharedPreferences.getString(KEY_GOALS, "[]")
        val type = object : TypeToken<List<Goal>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
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
        sharedPreferences.edit()
            .putString(KEY_MOOD, mood)
            .putString(KEY_MOOD_DATE, gson.toJson(Date()))
            .apply()
    }
    
    fun getCurrentMood(): Pair<String, Date>? {
        val mood = sharedPreferences.getString(KEY_MOOD, null)
        val dateJson = sharedPreferences.getString(KEY_MOOD_DATE, null)
        
        if (mood != null && dateJson != null) {
            val date = gson.fromJson(dateJson, Date::class.java)
            return Pair(mood, date)
        }
        return null
    }
}

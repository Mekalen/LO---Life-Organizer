package com.example.app1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.app1.data.DataManager
import com.example.app1.databinding.FragmentProfileBinding
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupClickListeners()
        loadProfileData()
    }
    
    private fun setupClickListeners() {
        binding.buttonExportData.setOnClickListener {
            // TODO: Implement data export functionality
        }
        
        binding.buttonClearData.setOnClickListener {
            showClearDataConfirmation()
        }
        
        binding.buttonAbout.setOnClickListener {
            showAboutDialog()
        }
    }
    
    private fun loadProfileData() {
        val tasks = dataManager.getTasks()
        val habits = dataManager.getHabits()
        val goals = dataManager.getGoals()
        val currentMood = dataManager.getCurrentMood()
        
        // Update statistics
        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.isCompleted }
        val completionRate = if (totalTasks > 0) (completedTasks * 100 / totalTasks) else 0
        
        val totalHabits = habits.size
        val averageStreak = if (totalHabits > 0) habits.map { it.currentStreak }.average().toInt() else 0
        
        val totalGoals = goals.size
        val averageProgress = if (totalGoals > 0) goals.map { it.progress }.average().toInt() else 0
        
        // Update UI
        binding.textTotalTasks.text = totalTasks.toString()
        binding.textCompletedTasks.text = completedTasks.toString()
        binding.textCompletionRate.text = "$completionRate%"
        
        binding.textTotalHabits.text = totalHabits.toString()
        binding.textAverageStreak.text = "$averageStreak days"
        
        binding.textTotalGoals.text = totalGoals.toString()
        binding.textAverageProgress.text = "$averageProgress%"
        
        // Update mood
        if (currentMood != null) {
            val (mood, date) = currentMood
            binding.textCurrentMood.text = mood
            binding.textMoodDate.text = dateFormat.format(date)
        } else {
            binding.textCurrentMood.text = "Not set"
            binding.textMoodDate.text = "N/A"
        }
        
        // Update join date (for now, use current date)
        binding.textJoinDate.text = dateFormat.format(Date())
    }
    
    private fun showClearDataConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("This will permanently delete all your tasks, habits, goals, and mood data. This action cannot be undone.")
            .setPositiveButton("Clear All Data") { _, _ ->
                clearAllData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun clearAllData() {
        // Clear all data by saving empty lists
        dataManager.saveTasks(emptyList())
        dataManager.saveHabits(emptyList())
        dataManager.saveGoals(emptyList())
        
        // Refresh the profile data
        loadProfileData()
    }
    
    private fun showAboutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("About App1")
            .setMessage("App1 - Personal Productivity App\n\nVersion: 1.0\n\nA simple and effective app to help you manage tasks, build habits, and achieve your goals.\n\nFeatures:\n• Task Management\n• Habit Tracking\n• Goal Setting\n• Mood Tracking\n• Local Data Storage")
            .setPositiveButton("OK", null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        loadProfileData()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.app1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.app1.data.DataManager
import com.example.app1.databinding.FragmentGraphsBinding

class GraphsFragment : Fragment() {
    private var _binding: FragmentGraphsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupViews()
        loadData()
    }
    
    private fun setupViews() {
        // For now, just display basic statistics
        binding.textPlaceholder.text = "📊 Progress Overview\n\nThis section will show visual charts of your progress.\n\nComing soon:\n• Task completion trends\n• Habit streak charts\n• Goal progress visualizations"
    }
    
    private fun loadData() {
        val tasks = dataManager.getTasks()
        val habits = dataManager.getHabits()
        val goals = dataManager.getGoals()
        
        val completedTasks = tasks.count { it.isCompleted }
        val totalTasks = tasks.size
        val completionRate = if (totalTasks > 0) (completedTasks * 100 / totalTasks) else 0
        
        val totalStreakDays = habits.sumOf { it.currentStreak }
        val averageGoalProgress = if (goals.isNotEmpty()) goals.map { it.progress }.average().toInt() else 0
        
        val statsText = "\n\n📈 Current Statistics:\n\n" +
                "✅ Tasks Completed: $completedTasks/$totalTasks ($completionRate%)\n" +
                "⭐ Total Habit Streak Days: $totalStreakDays\n" +
                "🎯 Average Goal Progress: $averageGoalProgress%"
        
        binding.textPlaceholder.text = binding.textPlaceholder.text.toString() + statsText
    }
    
    override fun onResume() {
        super.onResume()
        if (::dataManager.isInitialized) {
            loadData()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

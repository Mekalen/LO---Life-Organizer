package com.example.app1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app1.adapters.GoalsAdapter
import com.example.app1.data.DataManager
import com.example.app1.databinding.FragmentGoalsBinding
import com.example.app1.models.Goal
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class GoalsFragment : Fragment() {
    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    private lateinit var goalsAdapter: GoalsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupRecyclerView()
        setupClickListeners()
        loadGoals()
        
        // Update toolbar title
        (requireActivity() as MainActivity).updateToolbarTitle("Goals")
    }
    
    private fun setupRecyclerView() {
        goalsAdapter = GoalsAdapter(
            emptyList(),
            onGoalClick = { goal ->
                showEditGoalDialog(goal)
            }
        )
        
        binding.recyclerViewGoals.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = goalsAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddGoal.setOnClickListener {
            showAddGoalDialog()
        }
        
        binding.buttonAddGoal.setOnClickListener {
            showAddGoalDialog()
        }
    }
    
    private fun loadGoals() {
        val goals = dataManager.getGoals()
        if (goals.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.recyclerViewGoals.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.recyclerViewGoals.visibility = View.VISIBLE
            goalsAdapter.updateGoals(goals)
        }
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
                    loadGoals()
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
                    loadGoals()
                }
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete") { _, _ ->
                showDeleteConfirmation(goal)
            }
            .show()
    }
    
    private fun showDeleteConfirmation(goal: Goal) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Goal")
            .setMessage("Are you sure you want to delete '${goal.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                dataManager.deleteGoal(goal.id)
                loadGoals()
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
    
    override fun onResume() {
        super.onResume()
        loadGoals()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

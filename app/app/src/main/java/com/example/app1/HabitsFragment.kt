package com.example.app1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app1.adapters.HabitsAdapter
import com.example.app1.data.DataManager
import com.example.app1.databinding.FragmentHabitsBinding
import com.example.app1.models.Habit
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HabitsFragment : Fragment() {
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    private lateinit var habitsAdapter: HabitsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupRecyclerView()
        setupClickListeners()
        loadHabits()
    }
    
    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(
            emptyList(),
            onHabitClick = { habit ->
                showEditHabitDialog(habit)
            },
            onHabitTrack = { habit ->
                dataManager.trackHabit(habit.id)
                binding.recyclerViewHabits.post { loadHabits() }
            }
        )
        
        binding.recyclerViewHabits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitsAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
        
        binding.buttonAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }
    
    private fun loadHabits() {
        val habits = dataManager.getHabits()
        if (habits.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.recyclerViewHabits.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.recyclerViewHabits.visibility = View.VISIBLE
            habitsAdapter.updateHabits(habits)
        }
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
                    loadHabits()
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
                    binding.recyclerViewHabits.post { loadHabits() }
                }
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete") { _, _ ->
                showDeleteConfirmation(habit)
            }
            .show()
    }
    
    private fun showDeleteConfirmation(habit: Habit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete '${habit.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                dataManager.deleteHabit(habit.id)
                binding.recyclerViewHabits.post { loadHabits() }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

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
import com.example.app1.adapters.TasksAdapter
import com.example.app1.data.DataManager
import com.example.app1.databinding.FragmentTasksBinding
import com.example.app1.models.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    private lateinit var tasksAdapter: TasksAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupRecyclerView()
        setupClickListeners()
        loadTasks()
    }
    
    private fun setupRecyclerView() {
        tasksAdapter = TasksAdapter(
            emptyList(),
            onTaskClick = { task ->
                showEditTaskDialog(task)
            },
            onTaskComplete = { task, isCompleted ->
                val updatedTask = task.copy(isCompleted = isCompleted)
                dataManager.updateTask(updatedTask)
                // Defer reloading to avoid notifying during layout
                binding.recyclerViewTasks.post { loadTasks() }
            }
        )
        
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tasksAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
        
        binding.buttonAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }
    
    private fun loadTasks() {
        val tasks = dataManager.getTasks()
        if (tasks.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.recyclerViewTasks.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.recyclerViewTasks.visibility = View.VISIBLE
            tasksAdapter.updateTasks(tasks)
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
                    loadTasks()
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
                    binding.recyclerViewTasks.post { loadTasks() }
                }
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete") { _, _ ->
                showDeleteConfirmation(task)
            }
            .show()
    }
    
    private fun showDeleteConfirmation(task: Task) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete '${task.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                dataManager.deleteTask(task.id)
                binding.recyclerViewTasks.post { loadTasks() }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

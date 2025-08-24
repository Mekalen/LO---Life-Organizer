package com.example.app1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.app1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupBottomNavigation()
        
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment(), "Dashboard")
        }
    }

    private fun setupBottomNavigation() {
        binding.navDashboard.setOnClickListener {
            loadFragment(DashboardFragment(), "Dashboard")
        }
        
        binding.navTasks.setOnClickListener {
            loadFragment(TasksFragment(), "Tasks")
        }
        
        binding.navHabits.setOnClickListener {
            loadFragment(HabitsFragment(), "Habits")
        }
        
        binding.navGoals.setOnClickListener {
            loadFragment(GoalsFragment(), "Goals")
        }
        
        binding.navGraphs.setOnClickListener {
            loadFragment(GraphsFragment(), "Graphs")
        }
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        if (currentFragment?.javaClass != fragment.javaClass) {
            currentFragment = fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            
            binding.toolbar.title = title
        }
    }

    fun updateToolbarTitle(title: String) {
        binding.toolbar.title = title
    }
}

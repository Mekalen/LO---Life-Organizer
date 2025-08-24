package com.example.app1

import android.os.Bundle
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.app1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate() called on ${Build.MANUFACTURER} ${Build.MODEL}")

        try {
            // Samsung Galaxy A34 5G specific logging
            if (Build.MODEL.contains("A34", ignoreCase = true)) {
                Log.d("MainActivity", "Detected Samsung Galaxy A34 - applying compatibility mode")
            }
            
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            Log.d("MainActivity", "View binding successful")

            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            Log.d("MainActivity", "Toolbar setup successful")

            // Set up bottom navigation
            setupBottomNavigation()
            Log.d("MainActivity", "Bottom navigation setup successful")
            
            // Load diagnostic fragment first for Samsung devices to test compatibility
            if (savedInstanceState == null) {
                if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
                    Log.d("MainActivity", "Loading diagnostic fragment for Samsung device")
                    loadFragmentSafely(DiagnosticFragment(), "Diagnostic")
                } else {
                    loadFragmentSafely(DashboardFragment(), "Dashboard")
                }
                Log.d("MainActivity", "Default fragment loaded")
            }
            
            // Setup modern back press handling
            setupBackPressedHandler()
            Log.d("MainActivity", "Back press handler setup successful")
            
            Log.d("MainActivity", "onCreate() completed successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "Critical error in onCreate() - creating fallback UI", e)
            createFallbackUI(e.message ?: "Unknown error")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(DashboardFragment(), "Dashboard")
                    true
                }
                R.id.nav_tasks -> {
                    loadFragment(TasksFragment(), "Tasks")
                    true
                }
                R.id.nav_habits -> {
                    loadFragment(HabitsFragment(), "Habits")
                    true
                }
                R.id.nav_goals -> {
                    loadFragment(GoalsFragment(), "Goals")
                    true
                }
                R.id.nav_graphs -> {
                    loadFragment(GraphsFragment(), "Graphs")
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        try {
            Log.d("MainActivity", "Loading fragment: ${fragment.javaClass.simpleName}")
            if (currentFragment?.javaClass != fragment.javaClass) {
                currentFragment = fragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
                
                // Update toolbar title
                binding.toolbar.title = title
                Log.d("MainActivity", "Fragment loaded successfully: $title")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error loading fragment: ${fragment.javaClass.simpleName}", e)
        }
    }

    fun updateToolbarTitle(title: String) {
        binding.toolbar.title = title
    }
    
    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })
    }
    
    private fun loadFragmentSafely(fragment: Fragment, title: String) {
        try {
            Log.d("MainActivity", "Loading fragment safely: ${fragment.javaClass.simpleName}")
            currentFragment = fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            
            // Update toolbar title
            binding.toolbar.title = title
            Log.d("MainActivity", "Fragment loaded successfully: $title")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error loading fragment safely: ${fragment.javaClass.simpleName}", e)
            // Try loading diagnostic fragment as fallback
            try {
                val diagnostic = DiagnosticFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, diagnostic)
                    .commit()
                binding.toolbar.title = "Error Recovery"
            } catch (fallbackError: Exception) {
                Log.e("MainActivity", "Even diagnostic fragment failed", fallbackError)
                createFallbackUI("Fragment loading failed: ${e.message}")
            }
        }
    }
    
    private fun createFallbackUI(errorMessage: String) {
        try {
            Log.d("MainActivity", "Creating fallback UI due to: $errorMessage")
            
            // Create a simple layout programmatically
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(android.graphics.Color.WHITE)
                setPadding(64, 64, 64, 64)
            }
            
            val titleText = TextView(this).apply {
                text = "⚠️ App Recovery Mode"
                textSize = 24f
                setTextColor(android.graphics.Color.BLACK)
                setPadding(0, 0, 0, 32)
            }
            layout.addView(titleText)
            
            val errorText = TextView(this).apply {
                text = """
                    The app encountered an issue but is still running.
                    
                    Device: ${Build.MANUFACTURER} ${Build.MODEL}
                    Android: ${Build.VERSION.RELEASE}
                    
                    Error: $errorMessage
                    
                    Please check the logs for more details.
                    You can try restarting the app.
                """.trimIndent()
                textSize = 16f
                setTextColor(android.graphics.Color.DKGRAY)
            }
            layout.addView(errorText)
            
            setContentView(layout)
            Log.d("MainActivity", "Fallback UI created successfully")
            
        } catch (e: Exception) {
            Log.e("MainActivity", "Even fallback UI creation failed", e)
            // This is the absolute last resort
            val textView = TextView(this)
            textView.text = "CRITICAL ERROR - CHECK LOGS"
            textView.setBackgroundColor(android.graphics.Color.WHITE)
            textView.setTextColor(android.graphics.Color.BLACK)
            setContentView(textView)
        }
    }
}

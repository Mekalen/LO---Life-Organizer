package com.example.app1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class DiagnosticFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("DiagnosticFragment", "onCreateView() called - Creating simple diagnostic view")
        
        return try {
            // Create a simple layout programmatically to avoid XML issues
            val layout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(android.graphics.Color.WHITE)
                setPadding(64, 64, 64, 64)
            }
            
            // Add title
            val titleText = TextView(requireContext()).apply {
                text = "🎉 SUCCESS! App is Working!"
                textSize = 24f
                setTextColor(android.graphics.Color.BLACK)
                setPadding(0, 0, 0, 32)
            }
            layout.addView(titleText)
            
            // Add device info
            val deviceInfo = TextView(requireContext()).apply {
                text = """
                    ✅ Fragment loaded successfully
                    📱 Device: ${android.os.Build.MODEL}
                    🤖 Android: ${android.os.Build.VERSION.RELEASE}
                    🏗️ Manufacturer: ${android.os.Build.MANUFACTURER}
                    📦 App Version: Working
                    
                    If you can see this text, the core app functionality is working!
                    
                    Next steps:
                    • Try tapping other bottom navigation tabs
                    • The main issue was theme/color conflicts
                    • This confirms Samsung compatibility is working
                """.trimIndent()
                textSize = 16f
                setTextColor(android.graphics.Color.DKGRAY)
                setPadding(0, 16, 0, 16)
            }
            layout.addView(deviceInfo)
            
            // Add test button
            val testButton = android.widget.Button(requireContext()).apply {
                text = "Test Button - Tap Me!"
                setOnClickListener {
                    (it as android.widget.Button).text = "✅ Button Works!"
                    Log.d("DiagnosticFragment", "Button clicked - interactions working")
                }
            }
            layout.addView(testButton)
            
            Log.d("DiagnosticFragment", "Diagnostic view created successfully")
            layout
            
        } catch (e: Exception) {
            Log.e("DiagnosticFragment", "Error creating diagnostic view", e)
            // Fallback to absolute minimum
            TextView(requireContext()).apply {
                text = "BASIC FRAGMENT LOADED - CHECK LOGS"
                setBackgroundColor(android.graphics.Color.WHITE)
                setTextColor(android.graphics.Color.BLACK)
                setPadding(32, 32, 32, 32)
            }
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DiagnosticFragment", "onViewCreated() completed")
        
        // Update toolbar title if possible
        try {
            (activity as? MainActivity)?.updateToolbarTitle("🔧 Diagnostic Mode")
        } catch (e: Exception) {
            Log.w("DiagnosticFragment", "Could not update toolbar title", e)
        }
    }
}

package com.example.moodmate

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        val btnMoodSelection = findViewById<View>(R.id.btnMoodSelection)
        val btnMoodTracker = findViewById<View>(R.id.btnMoodTracker)
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)

        // Retrieve username from intent or shared preferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("MoodMatePrefs", MODE_PRIVATE)
        val username = intent.getStringExtra("USERNAME") ?: sharedPreferences.getString("USERNAME", "User") ?: "User"

        // Set the greeting with the actual username
        tvGreeting.text = "Hello, $username!"

        // Navigate to Mood Selection
        btnMoodSelection.setOnClickListener {
            startActivity(Intent(this, MoodSelectionActivity::class.java))
        }

        // Navigate to Mood Tracker
        btnMoodTracker.setOnClickListener {
            startActivity(Intent(this, MoodTrackerActivity::class.java))
        }

        // Logout functionality
        btnLogout.setOnClickListener {
            sharedPreferences.edit().clear().apply() // Clear user data

            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Ensures no back navigation
            }
            startActivity(intent)
            finish()
        }
    }
}

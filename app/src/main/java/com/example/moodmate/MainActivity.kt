package com.example.moodmate

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the logo ImageView
        val logo: ImageView = findViewById(R.id.logo)

        // Load looping animation
        val logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        logo.startAnimation(logoAnimation)

        // Make the whole screen clickable
        val fullscreenButton: FrameLayout = findViewById(R.id.fullscreenButton)
        fullscreenButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

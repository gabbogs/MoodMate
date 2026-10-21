package com.example.moodmate

import java.text.SimpleDateFormat
import java.util.Locale

// Shared display helpers for mood screens
object MoodUi {

    fun faceFor(mood: String): Int {
        return when (mood) {
            "Happy" -> R.drawable.face_happy
            "Sad" -> R.drawable.face_sad
            "Stressed" -> R.drawable.face_stressed
            "Excited" -> R.drawable.face_excited
            "Calm" -> R.drawable.face_calm
            "Angry" -> R.drawable.face_angry
            else -> R.drawable.mood_placeholder
        }
    }

    // MoodEntry stores dates as yyyy-MM-dd; show them as e.g. "Oct 21, 2026"
    fun formatDate(date: String): String {
        return try {
            val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
            if (parsed != null) SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(parsed) else date
        } catch (e: Exception) {
            date
        }
    }
}

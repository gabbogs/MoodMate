package com.example.moodmate

import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat

class MoodSuggestionActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_suggestion)

        val tvMoodTitle = findViewById<TextView>(R.id.tvMoodTitle)
        val tvMoodIntensity = findViewById<TextView>(R.id.tvMoodIntensity)
        val tvSuggestion = findViewById<TextView>(R.id.tvSuggestion)
        val moodIcon = findViewById<ImageView>(R.id.mood_icon)
        val btnBack = findViewById<Button>(R.id.btnBack)
        val actionButtonsLayout = findViewById<LinearLayout>(R.id.actionButtonsLayout)

        val mood = intent.getStringExtra("MOOD") ?: "Unknown"
        val intensity = intent.getIntExtra("INTENSITY", 5)

        tvMoodTitle.text = "Feeling ${mood.lowercase()}"
        tvMoodIntensity.text = "Intensity $intensity / 10"
        moodIcon.setImageResource(getMoodIcon(mood))

        val suggestion = getSuggestion(mood, intensity)
        tvSuggestion.text = suggestion

        val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 1000 }
        tvSuggestion.startAnimation(fadeIn)

        addQuickActionButtons(mood, intensity, actionButtonsLayout)

        if (mood == "Calm" || (mood == "Sad" && intensity > 7)) {
            playRelaxingMusic()
        }

        btnBack.setOnClickListener {
            stopMusic()
            finish()
        }
    }

    private fun addQuickActionButtons(mood: String, intensity: Int, layout: LinearLayout) {
        layout.removeAllViews()

        // Styled to match MM.Button (primary gradient pill)
        val button1 = AppCompatButton(this).apply {
            background = ContextCompat.getDrawable(context, R.drawable.mm_button_primary)
            setTextColor(Color.WHITE)
            textSize = 17f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            isAllCaps = false
            stateListAnimator = null
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (56 * resources.displayMetrics.density).toInt()
            )
            text = when (mood) {
                "Happy" -> "Share Happiness"
                "Sad" -> "Listen to Music"
                "Stressed" -> "Start Meditation"
                "Excited" -> "Plan an Activity"
                "Calm" -> "Read a Book"
                "Angry" -> "Do Deep Breathing"
                else -> "Try Something New"
            }
            setOnClickListener { performAction(mood) }
        }

        layout.addView(button1)
    }

    private fun performAction(mood: String) {
        when (mood) {
            "Happy" -> Toast.makeText(this, "Sharing happiness!", Toast.LENGTH_SHORT).show()
            "Sad" -> playRelaxingMusic()
            "Stressed" -> Toast.makeText(this, "Starting meditation...", Toast.LENGTH_SHORT).show()
            "Excited" -> Toast.makeText(this, "Planning an activity!", Toast.LENGTH_SHORT).show()
            "Calm" -> Toast.makeText(this, "Reading a book...", Toast.LENGTH_SHORT).show()
            "Angry" -> Toast.makeText(this, "Doing deep breathing...", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "No action available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSuggestion(mood: String, intensity: Int): String {
        return when (mood) {
            "Happy" -> when (intensity) {
                in 1..3 -> "Enjoy a peaceful moment, maybe read a book or listen to soft music."
                in 4..6 -> "Share your happiness with someone—send a kind message!"
                in 7..10 -> "Celebrate! Dance, sing, or call a friend to share your joy!"
                else -> "Keep smiling!"
            }
            "Sad" -> when (intensity) {
                in 1..3 -> "Take a short walk and get some fresh air."
                in 4..6 -> "Write down your thoughts in a journal or talk to a friend."
                in 7..10 -> "Listen to uplifting music or watch your favorite comedy show."
                else -> "You are not alone, reach out to someone you trust."
            }
            "Stressed" -> when (intensity) {
                in 1..3 -> "Take deep breaths and stretch for a few minutes."
                in 4..6 -> "Try meditation or a short break from work."
                in 7..10 -> "Consider a relaxing activity like yoga or listening to calming music."
                else -> "Take care of yourself and practice self-care."
            }
            "Excited" -> when (intensity) {
                in 1..3 -> "Channel your energy into creative activities."
                in 4..6 -> "Plan an outing or a fun activity with friends!"
                in 7..10 -> "Go all out—dance, celebrate, and share your excitement!"
                else -> "Stay enthusiastic and enjoy the moment!"
            }
            "Calm" -> when (intensity) {
                in 1..3 -> "Enjoy a quiet moment with tea or a good book."
                in 4..6 -> "Go for a relaxing walk or practice mindfulness."
                in 7..10 -> "Engage in deep relaxation like meditation or a spa day."
                else -> "Stay peaceful and embrace serenity."
            }
            "Angry" -> when (intensity) {
                in 1..3 -> "Take a few deep breaths and relax."
                in 4..6 -> "Write down your feelings or listen to calming music."
                in 7..10 -> "Try intense physical exercise or a calming activity."
                else -> "Release your anger in a healthy way."
            }
            else -> "Take care of yourself and try a relaxing activity."
        }
    }

    private fun getMoodIcon(mood: String): Int = MoodUi.faceFor(mood)

    private fun playRelaxingMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.relaxing_music).apply {
                isLooping = true
                start()
            }
        }
    }

    private fun stopMusic() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }
}

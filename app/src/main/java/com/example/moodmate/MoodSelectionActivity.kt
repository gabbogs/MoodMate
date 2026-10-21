package com.example.moodmate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoodSelectionActivity : AppCompatActivity() {

    private var selectedMood: String? = null
    private var moodIntensity: Int = 5
    private lateinit var tvIntensityValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_selection)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnHappy = findViewById<View>(R.id.btn_happy)
        val btnSad = findViewById<View>(R.id.btn_sad)
        val btnStressed = findViewById<View>(R.id.btn_stressed)
        val btnExcited = findViewById<View>(R.id.btn_excited)
        val btnCalm = findViewById<View>(R.id.btn_calm)
        val btnAngry = findViewById<View>(R.id.btn_angry)
        val seekBarMoodIntensity = findViewById<SeekBar>(R.id.seekBarMoodIntensity)
        val btnSaveMood = findViewById<Button>(R.id.btn_save_mood)
        tvIntensityValue = findViewById(R.id.tvIntensityValue)

        val moodButtons = mapOf(
            btnHappy to "Happy",
            btnSad to "Sad",
            btnStressed to "Stressed",
            btnExcited to "Excited",
            btnCalm to "Calm",
            btnAngry to "Angry"
        )

        for ((button, mood) in moodButtons) {
            button.setOnClickListener {
                selectedMood = mood
                highlightSelectedMood(button, moodButtons.keys)
            }
        }

        seekBarMoodIntensity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                moodIntensity = progress
                tvIntensityValue.text = "$progress / 10"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        btnSaveMood.setOnClickListener {
            if (selectedMood != null) {
                val sharedPreferences = getSharedPreferences("MoodMatePrefs", MODE_PRIVATE)
                val username = sharedPreferences.getString("USERNAME", null)
                val dbHelper = DatabaseHelper(this)
                val userId = dbHelper.getUserId(username ?: "")

                if (userId != -1) {
                    val moodEntry = MoodEntry(userId = userId, mood = selectedMood!!, intensity = moodIntensity)
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            MoodDatabase.getDatabase(this@MoodSelectionActivity).moodDao().insertMood(moodEntry)
                        }
                        Toast.makeText(this@MoodSelectionActivity, "Mood saved", Toast.LENGTH_SHORT).show()
                    }
                }

                val intent = Intent(this, MoodSuggestionActivity::class.java)
                intent.putExtra("MOOD", selectedMood)
                intent.putExtra("INTENSITY", moodIntensity)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a mood first!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // The selected cell gets a highlighted background (mm_mood_cell selector); the others fade back
    private fun highlightSelectedMood(selectedButton: View, allButtons: Collection<View>) {
        for (button in allButtons) {
            button.isSelected = button == selectedButton
            button.alpha = if (button == selectedButton) 1.0f else 0.55f
        }
    }
}

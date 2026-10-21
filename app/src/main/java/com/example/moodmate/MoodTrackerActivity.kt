package com.example.moodmate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoodTrackerActivity : AppCompatActivity() {

    private lateinit var moodListView: ListView
    private lateinit var emptyTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var moodDatabase: MoodDatabase
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_tracker)

        moodListView = findViewById(R.id.listMoodHistory)
        emptyTextView = findViewById(R.id.emptyTextView)
        progressBar = findViewById(R.id.progressBar)
        btnBack = findViewById(R.id.btnBack)

        moodDatabase = MoodDatabase.getDatabase(this)

        fetchMoodHistory()

        btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun fetchMoodHistory() {
        progressBar.visibility = View.VISIBLE

        val sharedPreferences = getSharedPreferences("MoodMatePrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", null)
        val dbHelper = DatabaseHelper(this)
        val userId = dbHelper.getUserId(username ?: "")

        if (userId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val moodEntries = moodDatabase.moodDao().getMoodHistory(userId)

                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE

                        if (moodEntries.isNotEmpty()) {
                            emptyTextView.visibility = View.GONE
                            moodListView.adapter = MoodHistoryAdapter(moodEntries)
                        } else {
                            emptyTextView.visibility = View.VISIBLE
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@MoodTrackerActivity, "Error loading moods", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Shows each entry as a card: mood face, mood name, date, and an intensity chip
    private inner class MoodHistoryAdapter(entries: List<MoodEntry>) :
        ArrayAdapter<MoodEntry>(this@MoodTrackerActivity, R.layout.item_mood_history, entries) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: layoutInflater.inflate(R.layout.item_mood_history, parent, false)
            val entry = getItem(position)!!
            view.findViewById<ImageView>(R.id.ivFace).setImageResource(MoodUi.faceFor(entry.mood))
            view.findViewById<TextView>(R.id.tvMood).text = entry.mood
            view.findViewById<TextView>(R.id.tvDate).text = MoodUi.formatDate(entry.date)
            view.findViewById<TextView>(R.id.tvIntensity).text = "${entry.intensity} / 10"
            return view
        }
    }
}

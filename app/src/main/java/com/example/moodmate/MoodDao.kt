package com.example.moodmate

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MoodDao {
    @Insert
    suspend fun insertMood(moodEntry: MoodEntry)

    @Query("SELECT * FROM mood_table WHERE userId = :userId ORDER BY date DESC")
    suspend fun getMoodHistory(userId: Int): List<MoodEntry>
}

package com.example.moodmate

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

class MoodUiTest {

    private lateinit var originalLocale: Locale

    @Before
    fun setUp() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun formatDate_showsReadableDate() {
        assertEquals("Mar 18, 2024", MoodUi.formatDate("2024-03-18"))
    }

    @Test
    fun formatDate_returnsInputWhenUnparseable() {
        assertEquals("not a date", MoodUi.formatDate("not a date"))
    }

    @Test
    fun faceFor_mapsEachMoodToItsFace() {
        assertEquals(R.drawable.face_happy, MoodUi.faceFor("Happy"))
        assertEquals(R.drawable.face_sad, MoodUi.faceFor("Sad"))
        assertEquals(R.drawable.face_stressed, MoodUi.faceFor("Stressed"))
        assertEquals(R.drawable.face_excited, MoodUi.faceFor("Excited"))
        assertEquals(R.drawable.face_calm, MoodUi.faceFor("Calm"))
        assertEquals(R.drawable.face_angry, MoodUi.faceFor("Angry"))
    }

    @Test
    fun faceFor_unknownMoodUsesPlaceholder() {
        assertEquals(R.drawable.mood_placeholder, MoodUi.faceFor("Bored"))
    }
}

package com.example.moodmate

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

// Runs on a device or emulator because PasswordHasher uses android.util.Base64
@RunWith(AndroidJUnit4::class)
class PasswordHasherTest {

    @Test
    fun hash_neverContainsThePlainPassword() {
        val stored = PasswordHasher.hash("Demo@1234")
        assertFalse(stored.contains("Demo@1234"))
        assertTrue(stored.startsWith("pbkdf2-"))
    }

    @Test
    fun verify_acceptsCorrectAndRejectsWrongPassword() {
        val stored = PasswordHasher.hash("Demo@1234")
        assertTrue(PasswordHasher.verify("Demo@1234", stored))
        assertFalse(PasswordHasher.verify("demo@1234", stored))
    }

    @Test
    fun hash_usesARandomSaltEachTime() {
        assertNotEquals(PasswordHasher.hash("Demo@1234"), PasswordHasher.hash("Demo@1234"))
    }

    @Test
    fun isLegacy_detectsPlainTextPasswords() {
        assertTrue(PasswordHasher.isLegacy("Demo@1234"))
        assertFalse(PasswordHasher.isLegacy(PasswordHasher.hash("Demo@1234")))
    }
}

package com.example.moodmate

import android.os.Build
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Salted PBKDF2 password hashing. Stored format: "pbkdf2-<alg>$<iterations>$<salt>$<hash>"
object PasswordHasher {

    private const val PREFIX = "pbkdf2-"
    private const val ITERATIONS = 120_000
    private const val SALT_BYTES = 16
    private const val KEY_BITS = 256

    // PBKDF2WithHmacSHA256 is available from API 26; older devices fall back to SHA-1
    private val algorithm: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) "PBKDF2WithHmacSHA256" else "PBKDF2WithHmacSHA1"

    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val alg = algorithm
        val hash = derive(password, salt, ITERATIONS, alg)
        return listOf(PREFIX + shortName(alg), ITERATIONS.toString(), encode(salt), encode(hash)).joinToString("$")
    }

    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split("$")
        if (parts.size != 4 || !parts[0].startsWith(PREFIX)) return false
        val alg = if (parts[0] == PREFIX + "sha256") "PBKDF2WithHmacSHA256" else "PBKDF2WithHmacSHA1"
        val iterations = parts[1].toIntOrNull() ?: return false
        val salt = decode(parts[2])
        val expected = decode(parts[3])
        val actual = derive(password, salt, iterations, alg)
        return MessageDigest.isEqual(expected, actual)
    }

    // Accounts created before hashing was added still hold the plain password
    fun isLegacy(stored: String): Boolean = !stored.startsWith(PREFIX)

    private fun derive(password: String, salt: ByteArray, iterations: Int, alg: String): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS)
        try {
            return SecretKeyFactory.getInstance(alg).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun shortName(alg: String) = if (alg.endsWith("SHA256")) "sha256" else "sha1"
    private fun encode(bytes: ByteArray) = Base64.encodeToString(bytes, Base64.NO_WRAP)
    private fun decode(text: String) = Base64.decode(text, Base64.NO_WRAP)
}

package com.example.moodmate

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var switchShowPassword: Switch
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.usernameInput)
        etPassword = findViewById(R.id.passwordInput)
        btnLogin = findViewById(R.id.loginButton)
        btnSignUp = findViewById(R.id.signUpButton)
        switchShowPassword = findViewById(R.id.showPasswordSwitch)

        dbHelper = DatabaseHelper(this)

        // Show/Hide Password Functionality
        switchShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            etPassword.setSelection(etPassword.text.length) // Keep cursor at end
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username and Password are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.authenticateUser(username, password)) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                // Save username to SharedPreferences
                val sharedPreferences = getSharedPreferences("MoodMatePrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("USERNAME", username)
                editor.apply()

                // Pass username to HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("USERNAME", username) // Passing through intent
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid Credentials!", Toast.LENGTH_SHORT).show()
            }
        }

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }
}
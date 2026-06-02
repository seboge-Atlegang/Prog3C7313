package com.example.uniprobudget

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.uniprobudget.data.AuthViewModel
import com.example.uniprobudget.data.RegisterResult
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)
        btnRegister = findViewById(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // Initialize ViewModel
        authViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[AuthViewModel::class.java]

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            authViewModel.register(username, email, password, confirmPassword)
        }

        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Observe results
        authViewModel.registerResult.observe(this) { result ->
            when (result) {
                is RegisterResult.Success -> {
                    Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                is RegisterResult.Error -> {
                    tvError.text = result.message
                    tvError.visibility = View.VISIBLE
                }
                null -> { }
            }
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnRegister.isEnabled = !isLoading
        }
    }
}
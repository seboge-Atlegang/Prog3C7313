package com.example.uniprobudget

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.uniprobudget.data.AuthViewModel
import com.example.uniprobudget.data.LoginResult
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views - MAKE SURE THESE IDs EXIST IN YOUR XML
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)
        btnLogin = findViewById(R.id.btnLogin)
        val tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)

        // Initialize ViewModel
        authViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[AuthViewModel::class.java]

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                tvError.text = "Please enter username"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                tvError.text = "Please enter password"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            authViewModel.login(username, password)
        }

        tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Observe results
        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(this, "Welcome ${result.user.username} to CashFlow!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is LoginResult.Error -> {
                    tvError.text = result.message
                    tvError.visibility = View.VISIBLE
                }
                null -> { }
            }
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !isLoading
        }
    }
}
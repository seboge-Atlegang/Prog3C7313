package com.example.uniprobudget.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)  // FIXED: pass application
    private val appDao = database.appDao()

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = appDao.login(username, password)
                if (user != null) {
                    _loginResult.value = LoginResult.Success(user)
                } else {
                    _loginResult.value = LoginResult.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Login failed: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _registerResult.value = RegisterResult.Error("All fields are required")
            return
        }
        if (username.length < 3) {
            _registerResult.value = RegisterResult.Error("Username must be at least 3 characters")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerResult.value = RegisterResult.Error("Invalid email address")
            return
        }
        if (password.length < 6) {
            _registerResult.value = RegisterResult.Error("Password must be at least 6 characters")
            return
        }
        if (password != confirmPassword) {
            _registerResult.value = RegisterResult.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val existingUser = appDao.getUserByUsername(username)
                if (existingUser != null) {
                    _registerResult.value = RegisterResult.Error("Username already taken")
                    _isLoading.value = false
                    return@launch
                }

                val existingEmail = appDao.getUserByEmail(email)
                if (existingEmail != null) {
                    _registerResult.value = RegisterResult.Error("Email already registered")
                    _isLoading.value = false
                    return@launch
                }

                val user = User(username = username, email = email, password = password)  // FIXED: includes email
                val userId = appDao.insertUser(user)
                if (userId > 0) {
                    _registerResult.value = RegisterResult.Success(userId)
                } else {
                    _registerResult.value = RegisterResult.Error("Registration failed")
                }
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error("Registration failed: ${e.message}")
            }
            _isLoading.value = false
        }
    }
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

sealed class RegisterResult {
    data class Success(val userId: Long) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}
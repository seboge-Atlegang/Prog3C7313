package com.example.uniprobudget.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Entity represents a registered user in the system
 * This table handles the login requirements
 */
@Entity(tableName = "users", indices = [androidx.room.Index(value = ["username"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String, // Identifier for login
    val email: String,
    val password: String  // Password for authentication
)
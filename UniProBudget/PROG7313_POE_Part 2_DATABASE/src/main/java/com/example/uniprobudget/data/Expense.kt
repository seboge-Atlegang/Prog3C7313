package com.example.uniprobudget.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This table is for tracking spending.
 * Stores user entered information by specifying date, start/end times, description, and category
 * and then the optional entry to add a photo of the expense.
 */
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,        // The monetary value of the expense
    val date: String,          // Format: YYYY-MM-DD
    val startTime: String,     // Format: HH:MM
    val endTime: String,       // Format: HH:MM
    val description: String,   // User entered description
    val categoryId: Int,       // Foreign key link to the Category table
    val photoPath: String? = null // String path to the stored image file (optional data)
)
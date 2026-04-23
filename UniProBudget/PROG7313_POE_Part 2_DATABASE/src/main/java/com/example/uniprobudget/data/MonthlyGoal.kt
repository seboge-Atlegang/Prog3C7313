package com.example.uniprobudget.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Entity for tracking budget targets.
 * User will set a minimum monthly goal as well as a maximum goal.
 * all that entered information will be stored here
 */
@Entity(tableName = "monthly_goals")
data class MonthlyGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monthYear: String, // Identifier for the month (e.g. April 2026)
    val minGoal: Double,   // Minimum spending target
    val maxGoal: Double    // Maximum spending limit
)
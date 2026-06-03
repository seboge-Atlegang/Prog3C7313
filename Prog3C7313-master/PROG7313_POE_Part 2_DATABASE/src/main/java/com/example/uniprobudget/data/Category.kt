package com.example.uniprobudget.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity handles expense categories (e.g. Groceries, Transport)
 * Allows users to be able to create categories.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String // The name of the category
)
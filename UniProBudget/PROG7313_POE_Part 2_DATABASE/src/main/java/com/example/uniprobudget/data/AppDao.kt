package com.example.uniprobudget.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/*
 * Data Access Object (DAO). Defines the SQL queries used by the app.
 * Room uses this interface to generate the actual implementation code.
 */
@Dao
interface AppDao {

    // Authentication
    @Query("SELECT * FROM users WHERE username = :user AND password = :pass LIMIT 1")
    suspend fun login(user: String, pass: String): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun register(user: User)

    // Expenses
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE date BETWEEN :start AND :end")
    fun getExpensesForPeriod(start: String, end: String): Flow<List<Expense>>

    // Categories
    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    /*
     * Calculate totals per category for a specific range.
     * This query sorts the amount field that is grouped by the category ID.
     */
    @Query("SELECT categoryId, SUM(amount) as total FROM expenses WHERE date BETWEEN :start AND :end GROUP BY categoryId")
    fun getCategoryTotals(start: String, end: String): Flow<List<CategorySummary>>

    // Goals
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGoal(goal: MonthlyGoal)
}

/*
 * Helper class to hold the result of the Category Totals query.
 */
data class CategorySummary(val categoryId: Int, val total: Double)
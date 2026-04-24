package com.example.uniprobudget.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/*
 * Data Access Object (DAO). Defines the SQL queries used by the app.
 * Room uses this interface to generate the actual implementation code.
 */
@Dao
interface AppDao {

    // ============ AUTHENTICATION ============

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    // ============ EXPENSES ============

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getExpensesForPeriod(startDate: String, endDate: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    // ============ CATEGORIES ============

    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    // ============ CATEGORY TOTALS ============

    @Query("SELECT categoryId, SUM(amount) as total FROM expenses WHERE date BETWEEN :startDate AND :endDate GROUP BY categoryId")
    fun getCategoryTotals(startDate: String, endDate: String): Flow<List<CategorySummary>>

    // ============ MONTHLY GOALS ============

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGoal(goal: MonthlyGoal)

    // FIXED: Changed 'month' to 'monthYear' to match MonthlyGoal entity
    @Query("SELECT * FROM monthly_goals WHERE monthYear = :monthValue")
    suspend fun getMonthlyGoal(monthValue: String): MonthlyGoal?

    @Query("SELECT * FROM monthly_goals")
    fun getAllMonthlyGoals(): Flow<List<MonthlyGoal>>
}

/*
 * Helper class to hold the result of the Category Totals query.
 */
data class CategorySummary(val categoryId: Int, val total: Double)
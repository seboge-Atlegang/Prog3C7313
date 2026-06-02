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

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getExpensesForPeriod(startDate: String, endDate: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("""
    SELECT * FROM expenses
    WHERE date BETWEEN :startDate AND :endDate
    ORDER BY date DESC
    """)
    fun getExpensesByDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<Expense>>

    // ============ CATEGORIES ============

    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Int)

    /**
     * ADVANCED REPORT: Calculates the sum of expenses grouped by category.
     * JOINs the categories table to get the name instead of just an ID.
     */
    @Query("""
        SELECT c.name as categoryName, SUM(e.amount) as totalAmount 
        FROM expenses e 
        JOIN categories c ON e.categoryId = c.id 
        WHERE e.date BETWEEN :startDate AND :endDate 
        GROUP BY c.name
    """)
    fun getCategoryTotals(startDate: String, endDate: String): Flow<List<CategorySummary>>


    /**
     * DASHBOARD REPORT: Gets total spending for a specific month pattern (e.g., "2026-04%")
     */
    @Query("SELECT SUM(amount) FROM expenses WHERE date LIKE :monthPattern")
    suspend fun getTotalSpentForMonth(monthPattern: String): Double?

    /**
     * GOAL LOOKUP: Retrieves the set budget for a specific month.
     */
    @Query("SELECT * FROM monthly_goals WHERE monthYear = :monthYear LIMIT 1")
    suspend fun getGoalForMonth(monthYear: String): MonthlyGoal?

    // ============ MONTHLY GOALS ============

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGoal(goal: MonthlyGoal)

    @Query("SELECT * FROM monthly_goals WHERE monthYear = :monthValue")
    suspend fun getMonthlyGoal(monthValue: String): MonthlyGoal?

    @Query("SELECT * FROM monthly_goals")
    fun getAllMonthlyGoals(): Flow<List<MonthlyGoal>>
}

/*
 * Helper class to hold the result of the Category Totals query.
 */
data class OriginalCategorySummary(val categoryId: Int, val total: Double)
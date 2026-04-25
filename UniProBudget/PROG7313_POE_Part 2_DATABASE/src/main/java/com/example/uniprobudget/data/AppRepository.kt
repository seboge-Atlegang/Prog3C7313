package com.example.uniprobudget.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * The Repository class abstracts access to the data sources.
 * This is the primary class members will use to interact with the database.
 */
class AppRepository(private val appDao: AppDao) {

    // Login and Registration
    suspend fun login(username: String, password: String): User? = appDao.login(username, password)
    suspend fun register(user: User): Long = appDao.insertUser(user)

    // Expense Management
    suspend fun addExpense(expense: Expense) = appDao.insertExpense(expense)
    fun getExpensesForPeriod(start: String, end: String): Flow<List<Expense>> = appDao.getExpensesForPeriod(start, end)
    fun getAllExpenses(): Flow<List<Expense>> = appDao.getAllExpenses()

    // Category Management
    suspend fun addCategory(category: Category) = appDao.insertCategory(category)
    val allCategories: Flow<List<Category>> = appDao.getAllCategories()
    suspend fun deleteCategory(categoryId: Int) = appDao.deleteCategory(categoryId)  // ADD THIS

    // Monthly Goals
    suspend fun updateGoal(goal: MonthlyGoal) = appDao.updateGoal(goal)
    suspend fun getGoalForMonth(monthYear: String): MonthlyGoal? = appDao.getGoalForMonth(monthYear)

    /**
     * Get spending breakdown for a date range (for Pie Charts or Lists)
     */
    fun getCategoryReport(start: String, end: String): Flow<List<CategorySummary>> {
        return appDao.getCategoryTotals(start, end)
    }

    /**
     * Get Budget Progress (for Progress Bars or Gauges)
     */
    fun getMonthlyBudgetStatus(monthYear: String): Flow<BudgetStatus> = flow {
        val total = appDao.getTotalSpentForMonth("$monthYear%") ?: 0.0
        val goal = appDao.getGoalForMonth(monthYear)

        val max = goal?.maxGoal ?: 0.0

        emit(BudgetStatus(
            totalSpent = total,
            minGoal = goal?.minGoal ?: 0.0,
            maxGoal = max,
            remainingToMax = max - total,
            isOverBudget = total > max && max > 0
        ))
    }
}
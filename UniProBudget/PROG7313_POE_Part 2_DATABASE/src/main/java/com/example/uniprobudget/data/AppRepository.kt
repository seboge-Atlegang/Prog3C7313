package com.example.uniprobudget.data

import kotlinx.coroutines.flow.Flow

/**
 * The Repository class abstracts access to the data sources.
 * This is the primary class members will use to interact with the database.
 */
class AppRepository(private val appDao: AppDao) {
    // Login and Registration
    suspend fun login(username: String, password: String): User? = appDao.login(username, password)
    suspend fun register(user: User): Long = appDao.insertUser(user)  // FIXED

    // Expense Management
    suspend fun addExpense(expense: Expense) = appDao.insertExpense(expense)
    fun getExpenses(start: String, end: String): Flow<List<Expense>> = appDao.getExpensesForPeriod(start, end)

    // Category Management
    suspend fun addCategory(category: Category) = appDao.insertCategory(category)
    val allCategories: Flow<List<Category>> = appDao.getAllCategories()
}
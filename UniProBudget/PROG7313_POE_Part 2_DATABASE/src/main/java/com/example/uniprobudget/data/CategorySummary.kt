package com.example.uniprobudget.data

/**
 * Used for the 'Category Totals' report.
 * Provides the UI with the name of the category and the total amount spent in it.
 */
data class CategorySummary(
    val categoryName: String,
    val totalAmount: Double
)

/**
 * Used for the 'Budget vs Actual' report/dashboard.
 * Tells the UI exactly how much is left in the budget.
 */
data class BudgetStatus(
    val totalSpent: Double,
    val minGoal: Double,
    val maxGoal: Double,
    val remainingToMax: Double,
    val isOverBudget: Boolean
)
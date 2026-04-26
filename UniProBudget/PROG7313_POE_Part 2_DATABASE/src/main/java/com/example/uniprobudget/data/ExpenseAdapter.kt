package com.example.uniprobudget.data

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expenses: List<Expense>,
    private val categories: List<Category>
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    inner class ViewHolder(private val tv: TextView) :
        RecyclerView.ViewHolder(tv) {

        fun bind(expense: Expense) {

            val categoryName = getCategoryName(expense.categoryId)

            val receiptStatus = if (expense.photoPath != null)
                "✓ Receipt Attached"
            else
                "✗ No Receipt"

            tv.text = """
────────────────────
Date: ${expense.date}
Category: $categoryName
Amount: R${expense.amount}

Description:
${expense.description}

$receiptStatus
────────────────────
            """.trimIndent()

            tv.textSize = 16f
            tv.setPadding(40, 40, 40, 40)
        }
    }

    private fun getCategoryName(categoryId: Int): String {
        return categories.find { it.id == categoryId }?.name ?: "Unknown"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = TextView(parent.context)
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount() = expenses.size
}
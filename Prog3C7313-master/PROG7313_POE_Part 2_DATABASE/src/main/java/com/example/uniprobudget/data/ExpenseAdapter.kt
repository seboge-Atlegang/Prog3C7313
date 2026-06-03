package com.example.uniprobudget.data

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expenses: List<Expense>,
    private val categories: List<Category>,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val layout: LinearLayout
    ) : RecyclerView.ViewHolder(layout) {

        private val tv = TextView(layout.context)
        private val deleteButton = Button(layout.context)

        init {

            tv.textSize = 16f
            tv.setPadding(40,40,40,20)

            deleteButton.text = "Delete Expense"

            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(25,25,25,25)

            layout.addView(tv)
            layout.addView(deleteButton)
        }

        fun bind(expense: Expense){

            val categoryName =
                categories.find {
                    it.id == expense.categoryId
                }?.name ?: "Unknown"

            val receiptStatus =
                if(expense.photoPath != null)
                    "✓ Receipt Attached"
                else
                    "✗ No Receipt"

            tv.text =
                """
────────────────────
Date: ${expense.date}
Category: $categoryName
Amount: R${expense.amount}

Description:
${expense.description}

$receiptStatus
────────────────────
""".trimIndent()

            deleteButton.setOnClickListener{
                onDeleteClick(expense)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType:Int
    ): ViewHolder {

        val layout =
            LinearLayout(parent.context)

        return ViewHolder(layout)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position:Int
    ){
        holder.bind(expenses[position])
    }

    override fun getItemCount() =
        expenses.size
}
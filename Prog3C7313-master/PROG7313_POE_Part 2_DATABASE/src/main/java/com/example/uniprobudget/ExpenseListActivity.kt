package com.example.uniprobudget

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uniprobudget.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpensesListActivity : AppCompatActivity() {

    private lateinit var repository: AppRepository

    private lateinit var rvExpenses: RecyclerView
    private lateinit var btnReset: Button
    private lateinit var btnFilter: Button
    private lateinit var btnAddExpense: Button

    private var job: Job? = null
    private var categoryList: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_list)

        val db = AppDatabase.getInstance(this)
        repository = AppRepository(db.appDao())

        rvExpenses = findViewById(R.id.rvExpenses)
        btnReset = findViewById(R.id.btnResetFilter)
        btnFilter = findViewById(R.id.btnFilter)
        btnAddExpense = findViewById(R.id.btnAddExpense)

        rvExpenses.layoutManager = LinearLayoutManager(this)

        btnAddExpense.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ExpenseTrackingActivity::class.java
                )
            )
        }

        btnFilter.setOnClickListener {
            showFilter()
        }

        btnReset.setOnClickListener {
            loadData()
        }

        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {

        job?.cancel()

        job = lifecycleScope.launch {

            categoryList = repository.allCategories.first()

            repository.getAllExpenses().collect { expenses ->

                rvExpenses.adapter =
                    ExpenseAdapter(
                        expenses,
                        categoryList
                    ) { expense ->
                        deleteExpense(expense)
                    }
            }
        }
    }

    private fun showFilter() {

        pickDate { startDate ->

            pickDate { endDate ->

                job?.cancel()

                job = lifecycleScope.launch {

                    repository.allCategories.collect { categories ->

                        categoryList = categories

                        repository.getExpensesForPeriod(
                            startDate,
                            endDate
                        ).collect { filtered ->

                            rvExpenses.adapter =
                                ExpenseAdapter(
                                    filtered,
                                    categoryList
                                ) { expense ->
                                    deleteExpense(expense)
                                }
                        }
                    }
                }
            }
        }
    }

    private fun deleteExpense(
        expense: Expense
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Expense")
            .setMessage(
                "Are you sure you want to delete this expense?"
            )
            .setPositiveButton("Delete") { _, _ ->

                lifecycleScope.launch {

                    repository.deleteExpense(expense)

                    Toast.makeText(
                        this@ExpensesListActivity,
                        "Expense deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    private fun pickDate(
        callback: (String) -> Unit
    ) {

        val cal = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->

                val selectedDate =
                    String.format(
                        "%04d-%02d-%02d",
                        year,
                        month + 1,
                        day
                    )

                callback(selectedDate)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
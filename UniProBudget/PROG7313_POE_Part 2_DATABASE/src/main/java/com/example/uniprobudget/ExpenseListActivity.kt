package com.example.uniprobudget

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
            startActivity(Intent(this, ExpenseTrackingActivity::class.java))
        }

        btnFilter.setOnClickListener {
            showFilter()
        }

        btnReset.setOnClickListener {
            loadData()
        }

        loadData()
    }

    private fun loadData() {

        job?.cancel()

        job = lifecycleScope.launch {

            // STEP 1: LOAD CATEGORIES FIRST (IMPORTANT FIX)
            categoryList = repository.allCategories.first()

            // STEP 2: NOW LOAD EXPENSES
            repository.getAllExpenses().collect { expenses ->

                rvExpenses.adapter = ExpenseAdapter(
                    expenses,
                    categoryList
                )
            }
        }
    }

    private fun showFilter() {

        pickDate { start ->

            pickDate { end ->

                job?.cancel()

                job = lifecycleScope.launch {

                    categoryList = repository.allCategories.first()

                    repository.getExpensesForPeriod(start, end)
                        .collect { filtered ->

                            if (filtered.isEmpty()) {
                                Toast.makeText(
                                    this@ExpensesListActivity,
                                    "No expenses found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            rvExpenses.adapter = ExpenseAdapter(
                                filtered,
                                categoryList
                            )
                        }
                }
            }
        }
    }

    private fun pickDate(callback: (String) -> Unit) {

        val cal = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, y, m, d ->
                callback(
                    String.format("%04d-%02d-%02d", y, m + 1, d)
                )
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
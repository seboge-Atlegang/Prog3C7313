package com.example.uniprobudget

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uniprobudget.data.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ReportsDashboardActivity : AppCompatActivity() {

    private lateinit var repository: AppRepository
    private lateinit var etMonth: TextInputEditText
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvMinGoal: TextView
    private lateinit var tvMaxGoal: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var progressBudget: ProgressBar
    private lateinit var rvCategoryTotals: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoData: TextView
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports_dashboard)

        try {
            val database = AppDatabase.getInstance(this)
            repository = AppRepository(database.appDao())

            etMonth = findViewById(R.id.etMonth)
            tvTotalSpent = findViewById(R.id.tvTotalSpent)
            tvMinGoal = findViewById(R.id.tvMinGoal)
            tvMaxGoal = findViewById(R.id.tvMaxGoal)
            tvRemaining = findViewById(R.id.tvRemaining)
            progressBudget = findViewById(R.id.progressBudget)
            rvCategoryTotals = findViewById(R.id.rvCategoryTotals)
            progressBar = findViewById(R.id.progressBar)
            tvNoData = findViewById(R.id.tvNoData)
            val btnLoadDashboard = findViewById<MaterialButton>(R.id.btnLoadDashboard)

            etMonth.setText("2026-04")
            rvCategoryTotals.layoutManager = LinearLayoutManager(this)

            btnLoadDashboard.setOnClickListener { loadDashboard() }
            loadDashboard()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun loadDashboard() {
        val monthYear = etMonth.text.toString().trim()
        if (monthYear.isEmpty()) {
            Toast.makeText(this, "Please enter a month (YYYY-MM)", Toast.LENGTH_SHORT).show()
            return
        }

        if (!monthYear.matches(Regex("\\d{4}-\\d{2}"))) {
            Toast.makeText(this, "Month must be YYYY-MM format (e.g., 2026-04)", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        tvNoData.visibility = View.GONE

        // Load budget status
        lifecycleScope.launch {
            try {
                repository.getMonthlyBudgetStatus(monthYear).collect { status ->
                    updateBudgetUI(status)
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                tvNoData.visibility = View.VISIBLE
                tvNoData.text = "Error loading budget: ${e.message}"
            }
        }

        // Load category totals for the month
        val startDate = "$monthYear-01"
        val endDate = "$monthYear-31"

        lifecycleScope.launch {
            try {
                repository.getCategoryReport(startDate, endDate).collect { categories ->
                    if (categories.isEmpty()) {
                        tvNoData.visibility = View.VISIBLE
                        tvNoData.text = "No expenses found for $monthYear"
                        rvCategoryTotals.visibility = View.GONE
                    } else {
                        tvNoData.visibility = View.GONE
                        rvCategoryTotals.visibility = View.VISIBLE
                        rvCategoryTotals.adapter = CategoryTotalsAdapter(categories)
                    }
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                tvNoData.visibility = View.VISIBLE
                tvNoData.text = "Error loading data: ${e.message}"
            }
        }
    }

    private fun updateBudgetUI(status: BudgetStatus) {
        tvTotalSpent.text = currencyFormat.format(status.totalSpent)
        tvMinGoal.text = "Min: ${currencyFormat.format(status.minGoal)}"
        tvMaxGoal.text = "Max: ${currencyFormat.format(status.maxGoal)}"
        tvRemaining.text = "Remaining: ${currencyFormat.format(status.remainingToMax)}"

        val progress = if (status.maxGoal > 0) {
            ((status.totalSpent / status.maxGoal) * 100).toInt()
        } else 0
        progressBudget.progress = progress.coerceIn(0, 100)

        if (status.isOverBudget) {
            tvTotalSpent.setTextColor(Color.RED)
            tvRemaining.setTextColor(Color.RED)
        } else {
            tvTotalSpent.setTextColor(Color.parseColor("#6200EE"))
            tvRemaining.setTextColor(Color.parseColor("#4CAF50"))
        }
    }

    inner class CategoryTotalsAdapter(private val categories: List<CategorySummary>) :
        RecyclerView.Adapter<CategoryTotalsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val textView = TextView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(32, 20, 32, 20)
                textSize = 15f
                setBackgroundResource(android.R.drawable.list_selector_background)
            }
            return ViewHolder(textView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val cat = categories[position]
            holder.textView.text = "${cat.categoryName.padEnd(20)} → ${currencyFormat.format(cat.totalAmount)}"
            holder.textView.setTextColor(Color.parseColor("#333333"))
        }

        override fun getItemCount(): Int = categories.size

        inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    }
}
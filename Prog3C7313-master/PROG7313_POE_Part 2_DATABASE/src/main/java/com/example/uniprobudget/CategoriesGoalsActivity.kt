package com.example.uniprobudget

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uniprobudget.data.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class CategoriesGoalsActivity : AppCompatActivity() {

    private lateinit var repository: AppRepository
    private lateinit var etCategoryName: TextInputEditText
    private lateinit var btnAddCategory: MaterialButton
    private lateinit var rvCategories: RecyclerView
    private lateinit var etGoalMonth: TextInputEditText
    private lateinit var etMinGoal: TextInputEditText
    private lateinit var etMaxGoal: TextInputEditText
    private lateinit var btnSaveGoal: MaterialButton
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar

    private var categoryList = listOf<Category>()
    private var categoryAdapter: CategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_goals)

        val database = AppDatabase.getInstance(this)
        repository = AppRepository(database.appDao())

        etCategoryName = findViewById(R.id.etCategoryName)
        btnAddCategory = findViewById(R.id.btnAddCategory)
        rvCategories = findViewById(R.id.rvCategories)
        etGoalMonth = findViewById(R.id.etGoalMonth)
        etMinGoal = findViewById(R.id.etMinGoal)
        etMaxGoal = findViewById(R.id.etMaxGoal)
        btnSaveGoal = findViewById(R.id.btnSaveGoal)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)

        rvCategories.layoutManager = LinearLayoutManager(this)
        etGoalMonth.setText("2026-04")

        loadCategories()
        loadCurrentGoal()

        btnAddCategory.setOnClickListener { addCategory() }
        btnSaveGoal.setOnClickListener { saveGoal() }
        etGoalMonth.setOnFocusChangeListener { _, _ -> loadCurrentGoal() }
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            repository.allCategories.collect { categories ->
                categoryList = categories
                categoryAdapter = CategoryAdapter(categories) { category, position ->
                    deleteCategory(category, position)
                }
                rvCategories.adapter = categoryAdapter
            }
        }
    }

    private fun loadCurrentGoal() {
        val monthYear = etGoalMonth.text.toString().trim()
        if (monthYear.isEmpty()) return

        lifecycleScope.launch {
            try {
                val goal = repository.getGoalForMonth(monthYear)
                goal?.let {
                    etMinGoal.setText(it.minGoal.toString())
                    etMaxGoal.setText(it.maxGoal.toString())
                } ?: run {
                    etMinGoal.text?.clear()
                    etMaxGoal.text?.clear()
                }
            } catch (e: Exception) {
                // No goal found, leave fields empty
            }
        }
    }

    private fun addCategory() {
        val name = etCategoryName.text.toString().trim()
        if (name.isEmpty()) {
            showError("Please enter a category name")
            return
        }

        if (categoryList.any { it.name.equals(name, ignoreCase = true) }) {
            showError("Category already exists!")
            return
        }

        progressBar.visibility = View.VISIBLE
        btnAddCategory.isEnabled = false

        lifecycleScope.launch {
            try {
                repository.addCategory(Category(name = name))
                Toast.makeText(this@CategoriesGoalsActivity, "Category added!", Toast.LENGTH_SHORT).show()
                etCategoryName.text?.clear()
                loadCategories()
            } catch (e: Exception) {
                showError("Failed to add category: ${e.message}")
            }
            progressBar.visibility = View.GONE
            btnAddCategory.isEnabled = true
        }
    }

    private fun saveGoal() {
        val monthYear = etGoalMonth.text.toString().trim()
        val minGoal = etMinGoal.text.toString().trim().toDoubleOrNull()
        val maxGoal = etMaxGoal.text.toString().trim().toDoubleOrNull()

        if (monthYear.isEmpty()) { showError("Enter month (YYYY-MM)"); return }
        if (!monthYear.matches(Regex("\\d{4}-\\d{2}"))) {
            showError("Month must be format: YYYY-MM (e.g., 2026-04)")
            return
        }
        if (minGoal == null) { showError("Enter minimum goal"); return }
        if (maxGoal == null) { showError("Enter maximum goal"); return }
        // REMOVED: if (minGoal > maxGoal) validation - users can now set any values they want

        progressBar.visibility = View.VISIBLE
        btnSaveGoal.isEnabled = false

        lifecycleScope.launch {
            try {
                val goal = MonthlyGoal(monthYear = monthYear, minGoal = minGoal, maxGoal = maxGoal)
                repository.updateGoal(goal)
                Toast.makeText(this@CategoriesGoalsActivity, "Goal saved for $monthYear!", Toast.LENGTH_LONG).show()
                etMinGoal.text?.clear()
                etMaxGoal.text?.clear()
            } catch (e: Exception) {
                showError("Failed to save goal: ${e.message}")
            }
            progressBar.visibility = View.GONE
            btnSaveGoal.isEnabled = true
        }
    }

    private fun deleteCategory(category: Category, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete '${category.name}'?\n\nNote: Expenses using this category will be orphaned.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    try {
                        repository.deleteCategory(category.id)
                        Toast.makeText(this@CategoriesGoalsActivity, "Category deleted!", Toast.LENGTH_SHORT).show()
                        loadCategories() // Refresh list
                    } catch (e: Exception) {
                        showError("Failed to delete category: ${e.message}")
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
        tvError.postDelayed({ tvError.visibility = View.GONE }, 3000)
    }

    inner class CategoryAdapter(
        private val categories: List<Category>,
        private val onDelete: (Category, Int) -> Unit
    ) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val textView = TextView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(32, 20, 32, 20)
                textSize = 16f
                setBackgroundResource(android.R.drawable.list_selector_background)
                isClickable = true
            }
            return CategoryViewHolder(textView)
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            holder.bind(categories[position], position)
        }

        override fun getItemCount(): Int = categories.size

        inner class CategoryViewHolder(private val textView: TextView) :
            RecyclerView.ViewHolder(textView) {
            fun bind(category: Category, position: Int) {
                textView.text = "📁 ${category.name}"
                textView.setTextColor(android.graphics.Color.parseColor("#333333"))

                textView.setOnLongClickListener {
                    onDelete(category, position)
                    true
                }
            }
        }
    }
}
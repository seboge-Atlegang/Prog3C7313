package com.example.uniprobudget

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.uniprobudget.data.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ExpenseTrackingActivity : AppCompatActivity() {

    private lateinit var repository: AppRepository
    private lateinit var etAmount: TextInputEditText
    private lateinit var etDate: TextInputEditText
    private lateinit var etStartTime: TextInputEditText
    private lateinit var etEndTime: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var spinnerCategory: AppCompatSpinner
    private lateinit var btnSaveExpense: MaterialButton
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAddPhoto: MaterialButton
    private lateinit var tvPhotoStatus: TextView

    private var categories = listOf<Category>()
    private var selectedCategoryId = 0
    private var currentPhotoPath: String? = null
    private var photoUri: Uri? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                tvPhotoStatus.text = "✓ Receipt photo attached"
                tvPhotoStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                photoUri = it
                currentPhotoPath = it.path
                tvPhotoStatus.text = "✓ Receipt photo attached from gallery"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_tracking)

        val database = AppDatabase.getInstance(this)
        repository = AppRepository(database.appDao())

        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        etStartTime = findViewById(R.id.etStartTime)
        etEndTime = findViewById(R.id.etEndTime)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSaveExpense = findViewById(R.id.btnSaveExpense)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)
        btnAddPhoto = findViewById(R.id.btnAddPhoto)
        tvPhotoStatus = findViewById(R.id.tvPhotoStatus)

        setupPhotoPicker()
        loadCategories()

        btnSaveExpense.setOnClickListener { saveExpense() }
    }

    private fun setupPhotoPicker() {
        btnAddPhoto.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Add Receipt Photo")
                .setItems(arrayOf("Camera", "Gallery", "Remove")) { _, which ->
                    when (which) {
                        0 -> {}
                        1 -> pickImageLauncher.launch("image/*")
                        2 -> {
                            currentPhotoPath = null
                            photoUri = null
                            tvPhotoStatus.text = "No photo"
                        }
                    }
                }.show()
        }
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            repository.allCategories.collect { list ->
                categories = list
                spinnerCategory.adapter =
                    ArrayAdapter(this@ExpenseTrackingActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        categories.map { it.name })
            }
        }
    }

    private fun saveExpense() {

        val expense = Expense(
            amount = etAmount.text.toString().toDouble(),
            date = etDate.text.toString(),
            startTime = etStartTime.text.toString(),
            endTime = etEndTime.text.toString(),
            description = etDescription.text.toString(),
            categoryId = selectedCategoryId,
            photoPath = currentPhotoPath
        )

        lifecycleScope.launch {
            repository.addExpense(expense)

            startActivity(
                Intent(this@ExpenseTrackingActivity,
                    ExpensesListActivity::class.java)
            )
            finish()
        }
    }

}
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

    // Register for camera result
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri?.let {
                tvPhotoStatus.text = "✓ Receipt photo attached"
                tvPhotoStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                currentPhotoPath = photoUri?.path
            }
        } else {
            tvPhotoStatus.text = "✗ Photo capture failed"
            tvPhotoStatus.setTextColor(android.graphics.Color.RED)
        }
    }

    // Register for gallery result
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            photoUri = it
            currentPhotoPath = getRealPathFromURI(it)
            tvPhotoStatus.text = "✓ Receipt photo attached from gallery"
            tvPhotoStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_tracking)

        try {
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
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupPhotoPicker() {
        btnAddPhoto.setOnClickListener {
            showPhotoDialog()
        }
    }

    private fun showPhotoDialog() {
        val options = arrayOf("📷 Take Photo with Camera", "🖼 Choose from Gallery", "🗑 Remove Photo")
        AlertDialog.Builder(this)
            .setTitle("Add Receipt Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> dispatchTakePictureIntent()
                    1 -> pickImageLauncher.launch("image/*")
                    2 -> {
                        currentPhotoPath = null
                        photoUri = null
                        tvPhotoStatus.text = "No photo attached"
                        tvPhotoStatus.setTextColor(android.graphics.Color.parseColor("#666666"))
                    }
                }
            }
            .show()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        "${packageName}.fileprovider",
                        it
                    )
                    photoUri = photoURI
                    takePictureLauncher.launch(photoURI)
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "Receipt_${timeStamp}_"
        val storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        return uri.path
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            try {
                repository.allCategories.collect { categoryList ->
                    categories = categoryList

                    if (categories.isEmpty()) {
                        Toast.makeText(this@ExpenseTrackingActivity,
                            "Please add categories first in Categories & Goals screen",
                            Toast.LENGTH_LONG).show()
                        btnSaveExpense.isEnabled = false

                        val emptyList = listOf("No categories available")
                        val adapter = ArrayAdapter<String>(this@ExpenseTrackingActivity,
                            android.R.layout.simple_spinner_item, emptyList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerCategory.adapter = adapter
                        return@collect
                    }

                    btnSaveExpense.isEnabled = true
                    val categoryNames = categories.map { it.name }
                    val adapter = ArrayAdapter<String>(this@ExpenseTrackingActivity,
                        android.R.layout.simple_spinner_item, categoryNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategory.adapter = adapter

                    spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            if (position < categories.size) {
                                selectedCategoryId = categories[position].id
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            selectedCategoryId = 0
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@ExpenseTrackingActivity, "Error loading categories: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveExpense() {
        try {
            val amountText = etAmount.text.toString().trim()
            val amount = if (amountText.isEmpty()) null else amountText.toDoubleOrNull()
            val date = etDate.text.toString().trim()
            val startTime = etStartTime.text.toString().trim()
            val endTime = etEndTime.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (categories.isEmpty()) {
                showError("Please add categories first in Categories & Goals screen")
                return
            }
            if (amount == null) { showError("Please enter a valid amount"); return }
            if (date.isEmpty()) { showError("Please enter a date (YYYY-MM-DD)"); return }
            if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                showError("Date must be YYYY-MM-DD (e.g., 2026-04-25)")
                return
            }
            if (startTime.isEmpty()) { showError("Please enter start time (HH:MM)"); return }
            if (endTime.isEmpty()) { showError("Please enter end time (HH:MM)"); return }
            if (description.isEmpty()) { showError("Please enter a description"); return }
            if (selectedCategoryId == 0) { showError("Please select a category"); return }

            progressBar.visibility = View.VISIBLE
            btnSaveExpense.isEnabled = false

            val expense = Expense(
                amount = amount,
                date = date,
                startTime = startTime,
                endTime = endTime,
                description = description,
                categoryId = selectedCategoryId,
                photoPath = currentPhotoPath  // Save photo path to database!
            )

            lifecycleScope.launch {
                try {
                    repository.addExpense(expense)
                    val photoMessage = if (currentPhotoPath != null) " with receipt photo!" else ""
                    Toast.makeText(this@ExpenseTrackingActivity, "✓ Expense saved$photoMessage", Toast.LENGTH_SHORT).show()
                    clearForm()
                } catch (e: Exception) {
                    showError("Failed to save: ${e.message}")
                }
                progressBar.visibility = View.GONE
                btnSaveExpense.isEnabled = true
            }
        } catch (e: Exception) {
            showError("Error: ${e.message}")
            progressBar.visibility = View.GONE
            btnSaveExpense.isEnabled = true
        }
    }

    private fun clearForm() {
        etAmount.text?.clear()
        etDate.text?.clear()
        etStartTime.text?.clear()
        etEndTime.text?.clear()
        etDescription.text?.clear()
        currentPhotoPath = null
        photoUri = null
        tvPhotoStatus.text = "No photo attached"
        tvPhotoStatus.setTextColor(android.graphics.Color.parseColor("#666666"))
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
        tvError.postDelayed({ tvError.visibility = View.GONE }, 3000)
    }
}
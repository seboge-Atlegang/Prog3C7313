package com.example.uniprobudget

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.lifecycleScope
import com.example.uniprobudget.data.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ExpenseTrackingActivity : AppCompatActivity() {

    private lateinit var repository: AppRepository

    private lateinit var etAmount: TextInputEditText
    private lateinit var etDate: TextInputEditText
    private lateinit var etStartTime: TextInputEditText
    private lateinit var etEndTime: TextInputEditText
    private lateinit var etDescription: TextInputEditText

    private lateinit var spinnerCategory: AppCompatSpinner

    private lateinit var btnSaveExpense: MaterialButton
    private lateinit var btnAddPhoto: MaterialButton

    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvPhotoStatus: TextView

    private var categories = listOf<Category>()
    private var selectedCategoryId = 0

    private var currentPhotoPath: String? = null
    private var photoUri: Uri? = null


    // Gallery picker
    private val pickImageLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {
                photoUri = it
                currentPhotoPath = it.path

                tvPhotoStatus.text =
                    "✓ Receipt photo attached from gallery"
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            R.layout.activity_expense_tracking
        )

        val database =
            AppDatabase.getInstance(this)

        repository =
            AppRepository(
                database.appDao()
            )

        etAmount =
            findViewById(R.id.etAmount)

        etDate =
            findViewById(R.id.etDate)

        etStartTime =
            findViewById(R.id.etStartTime)

        etEndTime =
            findViewById(R.id.etEndTime)

        etDescription =
            findViewById(R.id.etDescription)

        spinnerCategory =
            findViewById(R.id.spinnerCategory)

        btnSaveExpense =
            findViewById(R.id.btnSaveExpense)

        btnAddPhoto =
            findViewById(R.id.btnAddPhoto)

        tvError =
            findViewById(R.id.tvError)

        progressBar =
            findViewById(R.id.progressBar)

        tvPhotoStatus =
            findViewById(R.id.tvPhotoStatus)


        setupPhotoPicker()

        loadCategories()

        btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }


    private fun setupPhotoPicker() {

        btnAddPhoto.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Add Receipt Photo")
                .setItems(
                    arrayOf(
                        "Choose From Gallery",
                        "Remove Photo"
                    )
                ) { _, which ->

                    when(which){

                        0 -> {
                            pickImageLauncher.launch(
                                "image/*"
                            )
                        }

                        1 -> {
                            currentPhotoPath = null
                            photoUri = null

                            tvPhotoStatus.text =
                                "No photo attached"
                        }
                    }
                }
                .show()
        }
    }


    // FIXED CATEGORY LOADING
    private fun loadCategories() {

        lifecycleScope.launch {

            repository.allCategories.collect { list ->

                categories = list

                if(categories.isEmpty()){
                    Toast.makeText(
                        this@ExpenseTrackingActivity,
                        "Please create categories first",
                        Toast.LENGTH_LONG
                    ).show()

                    return@collect
                }

                val categoryNames =
                    categories.map { it.name }

                val adapter =
                    ArrayAdapter(
                        this@ExpenseTrackingActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        categoryNames
                    )

                spinnerCategory.adapter = adapter


                // VERY IMPORTANT FIX
                spinnerCategory.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: android.view.View?,
                            position: Int,
                            id: Long
                        ) {

                            selectedCategoryId =
                                categories[position].id
                        }

                        override fun onNothingSelected(
                            parent: AdapterView<*>?
                        ) {
                            selectedCategoryId = 0
                        }
                    }

                // Default selection
                if(categories.isNotEmpty()){
                    selectedCategoryId =
                        categories[0].id
                }
            }
        }
    }


    private fun saveExpense() {

        try {

            if(categories.isEmpty()){
                showError(
                    "Create a category first"
                )
                return
            }

            if(selectedCategoryId == 0){
                selectedCategoryId =
                    categories[0].id
            }

            val amount =
                etAmount.text.toString()
                    .trim()
                    .toDouble()

            val expense = Expense(
                amount = amount,
                date = etDate.text.toString(),
                startTime = etStartTime.text.toString(),
                endTime = etEndTime.text.toString(),
                description =
                    etDescription.text.toString(),
                categoryId =
                    selectedCategoryId,
                photoPath =
                    currentPhotoPath
            )

            progressBar.visibility =
                ProgressBar.VISIBLE

            lifecycleScope.launch {

                repository.addExpense(
                    expense
                )

                Toast.makeText(
                    this@ExpenseTrackingActivity,
                    "Expense saved",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this@ExpenseTrackingActivity,
                        ExpensesListActivity::class.java
                    )
                )

                finish()
            }

        }
        catch(e: Exception){
            showError(
                "Enter valid expense details"
            )
        }
    }


    private fun showError(
        message:String
    ){
        tvError.text = message
        tvError.visibility = TextView.VISIBLE
    }
}
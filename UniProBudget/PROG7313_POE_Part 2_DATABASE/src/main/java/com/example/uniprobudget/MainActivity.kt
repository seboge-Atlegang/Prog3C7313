package com.example.uniprobudget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnAddExpense).setOnClickListener {
            startActivity(Intent(this, ExpenseTrackingActivity::class.java))
        }

        findViewById<Button>(R.id.btnCategoriesGoals).setOnClickListener {
            startActivity(Intent(this, CategoriesGoalsActivity::class.java))
        }

        findViewById<Button>(R.id.btnReports).setOnClickListener {
            startActivity(Intent(this, ReportsDashboardActivity::class.java))
        }
    }
}
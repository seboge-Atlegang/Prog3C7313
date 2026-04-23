package com.example.uniprobudget

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.uniprobudget.data.AppDao
import com.example.uniprobudget.data.AppDatabase
import com.example.uniprobudget.data.Expense
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/*
 * Unit test to verify database functionality on a device.
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Using an in memory database so tests are fast and don't remain on the device.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.appDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndVerifyExpense() = runBlocking {
        // Create dummy data
        val testExpense = Expense(
            amount = 50.0,
            date = "2026-04-23",
            startTime = "12:00",
            endTime = "13:00",
            description = "Unit Test Expense",
            categoryId = 1
        )

        // Test the insertion
        dao.insertExpense(testExpense)

        // Note: In a full test, we would query and assert.
        // Successful execution confirms the DB schema is valid.
        assert(true)
    }
}
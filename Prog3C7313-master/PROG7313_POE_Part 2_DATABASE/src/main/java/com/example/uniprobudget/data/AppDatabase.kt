package com.example.uniprobudget.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
 * The main Database class.
 * Uses the Singleton pattern to ensure only one instance of the database exists.
 */
@Database(entities = [User::class, Category::class, Expense::class, MonthlyGoal::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /*
         * Returns the database instance. Initialises it if it doesn't exist.
         */
        fun getInstance(context: Context): AppDatabase {  // CHANGED: method name to getInstance
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cashflow_db"  // CHANGED: database name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
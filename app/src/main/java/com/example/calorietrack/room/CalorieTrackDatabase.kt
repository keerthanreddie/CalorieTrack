package com.example.calorietrack.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Meal::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
abstract class CalorieTrackDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: CalorieTrackDatabase? = null

        fun getInstance(context: Context): CalorieTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CalorieTrackDatabase::class.java,
                    "calorie_track_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

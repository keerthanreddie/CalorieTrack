package com.example.calorietrack.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MealDao {

    @Insert
    suspend fun insertMeal(meal: Meal)

    @Query("SELECT * FROM meals ORDER BY date DESC, id DESC")
    suspend fun getAllMeals(): List<Meal>

    @Query("SELECT * FROM meals WHERE date = :date ORDER BY id DESC")
    suspend fun getMealsForDate(date: String): List<Meal>

    @Query("SELECT SUM(proteinGrams) FROM meals WHERE date = :date")
    suspend fun getTotalProteinForDate(date: String): Int?
}

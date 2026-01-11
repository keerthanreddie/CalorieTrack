package com.example.calorietrack.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MealDao {

    @Insert
    suspend fun insertMeal(meal: Meal)

    // ✅ Meal History for a specific user only
    @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY date DESC, id DESC")
    suspend fun getAllMealsForUser(userId: String): List<Meal>

    // ✅ Meals for a specific user on a specific date
    @Query("SELECT * FROM meals WHERE userId = :userId AND date = :date ORDER BY id DESC")
    suspend fun getMealsForUserDate(userId: String, date: String): List<Meal>

    // ✅ Total protein for a specific user on a specific date
    @Query("SELECT SUM(proteinGrams) FROM meals WHERE userId = :userId AND date = :date")
    suspend fun getTotalProteinForUserDate(userId: String, date: String): Int?

    // OPTIONAL: useful if you want "Clear my meals" in future
    @Query("DELETE FROM meals WHERE userId = :userId")
    suspend fun deleteMealsForUser(userId: String)
}

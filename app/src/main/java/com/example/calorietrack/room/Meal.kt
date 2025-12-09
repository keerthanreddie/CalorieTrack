package com.example.calorietrack.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val proteinGrams: Int,
    val date: String // yyyy-MM-dd
)

package com.vlt.zeroperte.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FoodDao {

    @Query("SELECT * FROM food WHERE id IN (:foodId)")
    fun findById(foodId: Long): Food?

    @Query("SELECT * FROM food")
    fun allFoods(): List<Food>

    @Insert
    fun insert(food: Food)

    @Delete
    fun delete(food: Food)

    @Update
    fun update(food: Food)
}
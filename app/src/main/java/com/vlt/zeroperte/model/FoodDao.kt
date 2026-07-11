package com.vlt.zeroperte.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Dao
interface FoodDao {

    @Query("SELECT * FROM food WHERE id IN (:foodId)")
    fun findById(foodId: Long): Food?

    @Query("SELECT * FROM food WHERE name LIKE :name")
    fun findByName(name: String): List<Food>

    @Query("SELECT * FROM food WHERE category = :category")
    fun findByCategory(category: String): List<Food>

    @Query("SELECT * FROM food WHERE brand = :brand")
    fun findByBrand(brand: String): List<Food>

    @Query("SELECT * FROM food WHERE expiryDate >= date()")
    fun findExpired() : List<Food>

    @Query("SELECT * FROM food WHERE expiryDate >= date('now')" +
            "AND julianday(expiryDate) - julianday('now') < :days")
    fun findExpiringSinceDays(days: Int) : List<Food>

    @Query("SELECT * FROM food")
    fun allFoods(): List<Food>

    @Insert
    fun insert(food: Food)

    @Delete
    fun delete(food: Food)

    @Update
    fun update(food: Food)
}
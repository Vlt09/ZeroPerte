package com.vlt.zeroperte.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.DeleteTable
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

/*
    @Query("SELECT * FROM food WHERE name LIKE :name")
    suspend fun findByName(name: String): List<Food>

    @Query("SELECT * FROM food WHERE category = :category")
    suspend fun findByCategory(category: String): List<Food>

    @Query("SELECT * FROM food WHERE brand = :brand")
    suspend fun findByBrand(brand: String): List<Food>

    @Query("SELECT * FROM food WHERE expiryDate <= date()")
    suspend fun findExpired() : List<Food>

    @Query("SELECT * FROM food WHERE expiryDate >= date('now')" +
            "AND julianday(expiryDate) - julianday('now') < :days")
    suspend fun findExpiringSinceDays(days: Int) : List<Food>*/

    @Query("SELECT * FROM food")
    fun allFoods(): Flow<List<Food>>

    @Query("SELECT * FROM food WHERE id IN (:foodId)")
    suspend fun findById(foodId: Long): Food?

    @Insert
    suspend fun insert(food: Food)

    @Delete
    suspend fun delete(food: Food)

    @Update
    suspend fun update(food: Food)

    @Query("DELETE FROM food")
    suspend fun deleteAll()
}
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
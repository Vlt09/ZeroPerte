package com.vlt.zeroperte.data

import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.data.model.FoodDto
import com.vlt.zeroperte.data.model.FoodDao
import com.vlt.zeroperte.utils.FoodMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface FoodRepository {
    fun getAllFoods() : Flow<List<Food>>

    suspend fun getById(id: Long) : Food?

    suspend fun create(foodDto: FoodDto)

    suspend fun delete(foodDto: FoodDto)

    suspend fun update(foodDto: FoodDto)

}
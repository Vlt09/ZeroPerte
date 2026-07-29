package com.vlt.zeroperte.data

import com.vlt.zeroperte.data.FoodRepository
import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.data.model.FoodDto
import com.vlt.zeroperte.utils.FoodMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Double de test pour FoodRepository : pas de Room, juste un
 * MutableStateFlow que le test contrôle directement via setFoods().
 *
 * NOTE : adapte les méthodes ci-dessous si l'interface FoodRepository
 * réelle a une signature différente (noms, suspend ou non, etc.).
 */
class FakeFoodRepository : FoodRepository {

    private val foodsFlow = MutableStateFlow<List<Food>>(emptyList())

    override fun getAllFoods(): Flow<List<Food>> = foodsFlow

    override suspend fun create(foodDto: FoodDto) {
        foodsFlow.value += FoodMapper.fromDto(foodDto)
    }

    override suspend fun delete(foodDto: FoodDto) {
        foodsFlow.value -= FoodMapper.fromDto(foodDto)
    }

    override suspend fun update(foodDto: FoodDto) {
        val food = FoodMapper.fromDto(foodDto)
        foodsFlow.value = foodsFlow.value.map { if (it.id == food.id) food else it }
    }

    /** Permet au test de définir l'état actuel de la "base" simulée */
    fun setFoods(foods: List<Food>) {
        foodsFlow.value = foods
    }
}
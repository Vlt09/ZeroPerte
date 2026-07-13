package com.vlt.zeroperte.data

import com.vlt.zeroperte.data.model.FoodDto
import com.vlt.zeroperte.data.model.FoodDao
import com.vlt.zeroperte.utils.FoodMapper
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import javax.inject.Inject


@Module
@InstallIn(ActivityRetainedComponent::class)
class FoodRepository @Inject constructor(
    private val foodDao: FoodDao
) {
    suspend fun getAllFoods() = foodDao.allFoods()

    suspend fun getFoodExpired() = foodDao.findExpired()

    suspend fun getFoodById(foodId : Long) = foodDao.findById(foodId)

    suspend fun getFoodByName(name : String) = foodDao.findByName(name)

    suspend fun getFoodByBrand(brand : String) = foodDao.findByBrand(brand)

    suspend fun getFoodByCategory(category : String) = foodDao.findByCategory(category)

    suspend fun getFoodExpiringSinceDays(days : Int) = foodDao.findExpiringSinceDays(days)

    suspend fun create(foodDto: FoodDto) = foodDao.insert(FoodMapper.fromDto(foodDto))

    suspend fun delete(foodDto: FoodDto) = foodDao.delete(FoodMapper.fromDto(foodDto))

    suspend fun update(foodDto: FoodDto) = foodDao.update(FoodMapper.fromDto(foodDto))

}
package com.vlt.zeroperte.data

import com.vlt.zeroperte.data.model.FoodDto
import com.vlt.zeroperte.data.model.FoodDao
import com.vlt.zeroperte.utils.FoodMapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton


class DefaultFoodRepository @Inject constructor(
    private val foodDao: FoodDao
) : FoodRepository {
    override fun getAllFoods() = foodDao.allFoods()

    override suspend fun create(foodDto: FoodDto) = foodDao.insert(FoodMapper.fromDto(foodDto))

    override suspend fun delete(foodDto: FoodDto) = foodDao.delete(FoodMapper.fromDto(foodDto))

    override suspend fun update(foodDto: FoodDto) = foodDao.update(FoodMapper.fromDto(foodDto))

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFoodRepository(
        impl: DefaultFoodRepository
    ): FoodRepository
}

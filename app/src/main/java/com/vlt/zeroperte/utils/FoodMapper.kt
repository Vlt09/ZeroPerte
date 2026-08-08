package com.vlt.zeroperte.utils

import com.vlt.zeroperte.business.FoodStatusCalculator
import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.data.model.FoodDto
import com.vlt.zeroperte.data.model.FoodListViewModelDto

object FoodMapper {

    /**
     * Converts a [FoodDto] into a [Food] entity.
     *
     * Note: if any of the entity's required fields are missing
     * from the DTO (`name`, `expiryDate`, or `amount`), the
     * function returns `null` instead of a partially valid entity.
     *
     * @param foodDto the DTO to convert
     * @return the corresponding [Food] entity, or `null` if a required field is missing
     */
    fun fromDto(foodDto: FoodDto) : Food {
        return Food(id = foodDto.id, name = foodDto.name, brand = foodDto.brand,
            category = foodDto.category, datePurchased = foodDto.datePurchased,
            expiryDate = foodDto.expiryDate, comment = foodDto.comment, amount = foodDto.amount)
    }

    fun toFoodListViewModelDto(food: Food) : FoodListViewModelDto {
        return FoodListViewModelDto(
            food.name, food.brand, food.category, food.datePurchased,
            food.expiryDate, food.comment, food.amount,
            FoodStatusCalculator.fromExpiryDate(food.expiryDate), food.id
        )
    }

    fun toFoodDto(food: Food) : FoodDto{
        return FoodDto(
            food.name, food.brand, food.category, food.datePurchased,
            food.expiryDate, food.comment, food.amount,
            food.id
        )
    }

    fun toFoodDto(dto: FoodListViewModelDto): FoodDto {
        return FoodDto(
            name = dto.name,
            brand = dto.brand,
            category = dto.category,
            datePurchased = dto.datePurchased,
            expiryDate = dto.expiryDate,
            comment = dto.comment,
            amount = dto.amount,
            id = dto.id
        )
    }

}
package com.vlt.zeroperte.utils

import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.data.model.FoodDto

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
        return Food(name = foodDto.name, brand = foodDto.brand, category = foodDto.category,
            datePurchased = foodDto.datePurchased, expiryDate = foodDto.expiryDate,
            comment = foodDto.comment, amount = foodDto.amount)
    }


}
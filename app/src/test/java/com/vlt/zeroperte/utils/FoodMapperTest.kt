package com.vlt.zeroperte.utils

import com.vlt.zeroperte.business.FoodStatusCalculator
import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.data.model.FoodDto
import com.vlt.zeroperte.data.model.domain.FoodStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class FoodMapperTest {

    private fun sampleFoodDto(
        name: String = "Yaourt",
        brand: String? = "Danone",
        category: String? = "frais",
        amount: Int? = 4,
        datePurchased: LocalDate? = LocalDate.now().minusDays(2),
        expiryDate: LocalDate = LocalDate.now().plusDays(30),
        comment: String? = null,
        id: Long = 42L
    ) = FoodDto(
        name = name,
        brand = brand,
        category = category,
        datePurchased = datePurchased,
        expiryDate = expiryDate,
        comment = comment,
        amount = amount,
        id = id
    )

    private fun sampleFood(
        id: Long = 42L,
        name: String = "Yaourt",
        brand: String? = "Danone",
        category: String? = "frais",
        amount: Int? = 4,
        datePurchased: LocalDate? = LocalDate.now().minusDays(2),
        expiryDate: LocalDate = LocalDate.now().plusDays(30),
        comment: String? = null
    ) = Food(
        id = id,
        name = name,
        brand = brand,
        category = category,
        datePurchased = datePurchased,
        expiryDate = expiryDate,
        comment = comment,
        amount = amount
    )

    @Test
    fun `fromDto maps every field to the corresponding Food entity`() {
        val dto = sampleFoodDto()

        val food = FoodMapper.fromDto(dto)

        assertEquals(dto.id, food.id)
        assertEquals(dto.name, food.name)
        assertEquals(dto.brand, food.brand)
        assertEquals(dto.category, food.category)
        assertEquals(dto.datePurchased, food.datePurchased)
        assertEquals(dto.expiryDate, food.expiryDate)
        assertEquals(dto.comment, food.comment)
        assertEquals(dto.amount, food.amount)
    }

    @Test
    fun `toFoodDto maps every field to the corresponding FoodDto`() {
        val food = sampleFood()

        val dto = FoodMapper.toFoodDto(food)

        assertEquals(food.id, dto.id)
        assertEquals(food.name, dto.name)
        assertEquals(food.brand, dto.brand)
        assertEquals(food.category, dto.category)
        assertEquals(food.datePurchased, dto.datePurchased)
        assertEquals(food.expiryDate, dto.expiryDate)
        assertEquals(food.comment, dto.comment)
        assertEquals(food.amount, dto.amount)
    }

    @Test
    fun `toFoodListViewModelDto maps every field and computes the status`() {
        val food = sampleFood(expiryDate = LocalDate.now().minusDays(1))

        val dto = FoodMapper.toFoodListViewModelDto(food)

        assertEquals(food.id, dto.id)
        assertEquals(food.name, dto.name)
        assertEquals(food.brand, dto.brand)
        assertEquals(food.category, dto.category)
        assertEquals(food.datePurchased, dto.datePurchased)
        assertEquals(food.expiryDate, dto.expiryDate)
        assertEquals(food.comment, dto.comment)
        assertEquals(food.amount, dto.amount)
        assertEquals(FoodStatusCalculator.fromExpiryDate(food.expiryDate), dto.status)
        assertEquals(FoodStatus.Expired, dto.status)
    }

    @Test
    fun `toFoodDto from FoodListViewModelDto maps every field back`() {
        val food = sampleFood()
        val viewModelDto = FoodMapper.toFoodListViewModelDto(food)

        val dto = FoodMapper.toFoodDto(viewModelDto)

        assertEquals(viewModelDto.id, dto.id)
        assertEquals(viewModelDto.name, dto.name)
        assertEquals(viewModelDto.brand, dto.brand)
        assertEquals(viewModelDto.category, dto.category)
        assertEquals(viewModelDto.datePurchased, dto.datePurchased)
        assertEquals(viewModelDto.expiryDate, dto.expiryDate)
        assertEquals(viewModelDto.comment, dto.comment)
        assertEquals(viewModelDto.amount, dto.amount)
    }

    @Test
    fun `fromDto then toFoodDto round-trips to an equal FoodDto`() {
        val dto = sampleFoodDto()

        val roundTripped = FoodMapper.toFoodDto(FoodMapper.fromDto(dto))

        assertEquals(dto, roundTripped)
    }
}

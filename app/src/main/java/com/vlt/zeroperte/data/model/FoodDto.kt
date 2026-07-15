package com.vlt.zeroperte.data.model

import com.vlt.zeroperte.data.model.domain.FoodStatus
import java.time.LocalDate

data class FoodDto(val name: String, val brand: String?, val category: String?,
                         val datePurchased: LocalDate?, val expiryDate: LocalDate, val comment: String?, val amount: Int?)

data class FoodListViewModelDto(val name: String, val brand: String?, val category: String?,
                                val datePurchased: LocalDate?, val expiryDate: LocalDate,
                                val comment: String?, val amount: Int?, val status : FoodStatus
)
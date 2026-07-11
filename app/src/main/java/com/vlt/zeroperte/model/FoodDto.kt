package com.vlt.zeroperte.model

import java.time.LocalDate

data class FoodDto(val name: String?, val brand: String?, val category: String?,
                   val datePurchased: LocalDate?, val expiryDate: LocalDate?, val comment: String?, val amount: Int?)

data class FoodPostDto(val name: String, val brand: String?, val category: String?,
                   val datePurchased: LocalDate?, val expiryDate: LocalDate, val comment: String?, val amount: Int)
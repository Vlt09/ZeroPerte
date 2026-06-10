package com.zeroperte.model

import kotlinx.datetime.LocalDateTime

data class FoodDto(val name: String?, val brand: String?, val category: String?,
                   val datePurchased: LocalDateTime?, val expiryDate: LocalDateTime?, val comment: String?, val amount: Int?)

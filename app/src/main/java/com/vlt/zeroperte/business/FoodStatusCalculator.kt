package com.vlt.zeroperte.business

import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class FoodStatus{
    OK,
    SOON_EXPIRED,
    EXPIRED
}


object FoodStatusCalculator {
    const val soonExpiredDays = 5

    fun fromExpiryDate(expiryDate : LocalDate) : FoodStatus{
        val daysDiff = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate)

        if (daysDiff <= 0) return FoodStatus.EXPIRED
        if (daysDiff <= soonExpiredDays) return FoodStatus.SOON_EXPIRED
        else return FoodStatus.OK
    }
}
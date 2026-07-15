package com.vlt.zeroperte.business

import com.vlt.zeroperte.data.model.domain.FoodStatus
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit


object FoodStatusCalculator {
    const val soonExpiredDays = 5

    fun fromExpiryDate(expiryDate : LocalDate) : FoodStatus {
        val daysDiff = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate)

        if (daysDiff <= 0) return FoodStatus.Expired
        if (daysDiff <= soonExpiredDays) return FoodStatus.ExpiringSoon
        else return FoodStatus.Edible
    }
}
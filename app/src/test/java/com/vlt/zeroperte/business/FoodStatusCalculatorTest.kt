package com.vlt.zeroperte.business

import com.vlt.zeroperte.data.model.domain.FoodStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class FoodStatusCalculatorTest {

    @Test
    fun `expiry date in the past returns EXPIRED`() {
        val expiryDate = LocalDate.now().minusDays(3)

        val status = FoodStatusCalculator.fromExpiryDate(expiryDate)

        assertEquals(FoodStatus.Expired, status)
    }

    @Test
    fun `expiry date today returns EXPIRED`() {
        val expiryDate = LocalDate.now()

        val status = FoodStatusCalculator.fromExpiryDate(expiryDate)

        assertEquals(FoodStatus.Expired, status)
    }

    @Test
    fun `expiry date one day in the future returns SOON_EXPIRED`() {
        val expiryDate = LocalDate.now().plusDays(1)

        val status = FoodStatusCalculator.fromExpiryDate(expiryDate)

        assertEquals(FoodStatus.ExpiringSoon, status)
    }

    @Test
    fun `expiry date exactly at soonExpiredDays threshold returns SOON_EXPIRED`() {
        val expiryDate = LocalDate.now().plusDays(FoodStatusCalculator.soonExpiredDays.toLong())

        val status = FoodStatusCalculator.fromExpiryDate(expiryDate)

        assertEquals(FoodStatus.ExpiringSoon, status)
    }

    @Test
    fun `expiry date one day beyond soonExpiredDays threshold returns Edible`() {
        val expiryDate = LocalDate.now().plusDays(FoodStatusCalculator.soonExpiredDays.toLong() + 1)

        val status = FoodStatusCalculator.fromExpiryDate(expiryDate)

        assertEquals(FoodStatus.Edible, status)
    }

    @Test
    fun `expiry date far in the future returns Edible`() {
        val expiryDate = LocalDate.now().plusDays(60)

        val status = FoodStatusCalculator.fromExpiryDate(expiryDate)

        assertEquals(FoodStatus.Edible, status)
    }
}
package com.vlt.zeroperte.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `fromStringFormat parses an ISO date string into a LocalDate`() {
        val result = converters.fromStringFormat("2026-09-01")

        assertEquals(LocalDate.of(2026, 9, 1), result)
    }

    @Test
    fun `fromStringFormat returns null for a null value`() {
        assertNull(converters.fromStringFormat(null))
    }

    @Test
    fun `dateToStringDate formats a LocalDate into an ISO date string`() {
        val result = converters.dateToStringDate(LocalDate.of(2026, 9, 1))

        assertEquals("2026-09-01", result)
    }

    @Test
    fun `dateToStringDate returns null for a null value`() {
        assertNull(converters.dateToStringDate(null))
    }

    @Test
    fun `fromStringFormat and dateToStringDate round-trip a date`() {
        val date = LocalDate.of(2025, 12, 31)

        val roundTripped = converters.fromStringFormat(converters.dateToStringDate(date))

        assertEquals(date, roundTripped)
    }

    @Test
    fun `fromStringDateToDate parses a dd-MM-yyyy date into the matching Date`() {
        val result = Converters.fromStringDateToDate("25/12/2026")

        val localDate = result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        assertEquals(LocalDate.of(2026, 12, 25), localDate)
    }
}

package com.vlt.zeroperte.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import kotlin.reflect.full.declaredMemberProperties

@Serializable
data class Food(
    val id: Long, val name: String, val brand: String?, val category: String?,
    val datePurchased: LocalDate?, val expiryDate: LocalDate, val comment: String?, val amount: Int
) {
    companion object {
        fun getMemberPropertiesString() : List<String> {
            return Food::class.declaredMemberProperties.map { p -> p.name }.toList()
        }

        val converters: Map<String, (String) -> Any?> = mapOf(
            "name" to { s: String -> s },
            "brand" to { s: String -> s },
            "category" to { s: String -> s },
            "datePurchased" to { s: String -> if (s == "") null else LocalDate.parse(s) },
            "expiryDate" to { s: String -> if (s == "") null else LocalDate.parse(s) },
            "comment" to { s: String -> s },
            "amount" to { s: String -> if (s == "") null else s.toInt() }
        )
    }
}


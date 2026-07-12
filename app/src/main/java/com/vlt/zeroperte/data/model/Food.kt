package com.vlt.zeroperte.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.time.LocalDate

@Entity
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "brand") val brand: String?,
    @ColumnInfo(name = "category") val category: String?,
    @ColumnInfo(name = "datePurchased") val datePurchased: LocalDate?,
    @ColumnInfo(name = "expiryDate") val expiryDate: LocalDate,
    @ColumnInfo(name = "comment") val comment: String?,
    @ColumnInfo(name = "amount") val amount: Int
) {
    companion object {
        fun getMemberPropertiesString() : List<String> {
            return listOf("id", "name", "brand", "category", "datePurchased", "expiryDate",
                "comment", "amount")
        }

        internal val formatter = SimpleDateFormat("yyyy-MM-dd")

        val converters: Map<String, (String) -> Any?> = mapOf(
            "name" to { s: String -> s },
            "brand" to { s: String -> s },
            "category" to { s: String -> s },
            "datePurchased" to { s: String -> if (s == "") null else formatter.parse(s) },
            "expiryDate" to { s: String -> if (s == "") null else formatter.parse(s) },
            "comment" to { s: String -> s },
            "amount" to { s: String -> if (s == "") null else s.toInt() }
        )
    }
}


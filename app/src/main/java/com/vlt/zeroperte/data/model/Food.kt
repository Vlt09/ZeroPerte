package com.vlt.zeroperte.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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
    @ColumnInfo(name = "amount") val amount: Int?
)


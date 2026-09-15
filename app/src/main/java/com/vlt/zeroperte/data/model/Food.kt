package com.vlt.zeroperte.data.model

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vlt.zeroperte.R
import java.time.LocalDate

enum class FoodCategory(@StringRes val labelId: Int){
    FRUIT_VEGETABLE(R.string.food_category_fruit_vegetable),
    STARCHY(R.string.food_category_starchy),
    LEGUME(R.string.food_category_legume),
    DAIRY(R.string.food_category_dairy),
    FAT_OIL(R.string.food_category_fat_oil),
    MEAT(R.string.food_category_meat),
    FISH_SEAFOOD(R.string.food_category_fish_seafood),
    SUGARY(R.string.food_category_sugary),
    DRINK(R.string.food_category_drink),
    WATER(R.string.food_category_water)

}

@Entity
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "brand") val brand: String?,
    @ColumnInfo(name = "category") val category: FoodCategory?,
    @ColumnInfo(name = "datePurchased") val datePurchased: LocalDate?,
    @ColumnInfo(name = "expiryDate") val expiryDate: LocalDate,
    @ColumnInfo(name = "comment") val comment: String?,
    @ColumnInfo(name = "amount") val amount: Int?
)


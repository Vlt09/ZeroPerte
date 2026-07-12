package com.vlt.zeroperte.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.data.model.FoodDao
import com.vlt.zeroperte.utils.Converters

@Database(entities = [Food::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
}
package com.vlt.zeroperte.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.data.model.FoodDao

@Database(entities = [Food::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
}
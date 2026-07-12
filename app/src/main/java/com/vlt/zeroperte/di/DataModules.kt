package com.vlt.zeroperte.di

import android.content.Context
import androidx.room.Room
import com.vlt.zeroperte.data.model.FoodDao
import com.vlt.zeroperte.data.source.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "Food.db"
        ).build()
    }

    @Provides
    fun provideFoodDao(database: AppDatabase): FoodDao = database.foodDao()
}
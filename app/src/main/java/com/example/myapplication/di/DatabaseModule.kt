package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.model.data.local.AppDatabase
import com.example.myapplication.model.data.local.dao.CharacterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "character_database_v3"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCharacterDao(
        database: AppDatabase
    ): CharacterDao =
        database.characterDao()
}
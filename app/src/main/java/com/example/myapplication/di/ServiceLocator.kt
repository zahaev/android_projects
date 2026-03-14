package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.model.data.local.AppDatabase
import com.example.myapplication.model.data.local.CharacterLocalDataSource
import com.example.myapplication.model.data.remote.CharacterRemoteDataSource
import com.example.myapplication.model.data.remote.RetrofitClient
import com.example.myapplication.model.data.repository.CharacterRepositoryImpl
import com.example.myapplication.model.domain.repository.CharacterRepository

object ServiceLocator {
    fun provideCharacterRepository(context: Context): CharacterRepository {
        val database = AppDatabase.getDatabase(context)
        val dao = database.characterDao()
        val localDataSource = CharacterLocalDataSource(dao)
        val remoteDataSource = CharacterRemoteDataSource(RetrofitClient.api)

        return CharacterRepositoryImpl(
            localDataSource,
            remoteDataSource
        )
    }
}
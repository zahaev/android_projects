package com.example.myapplication.di

import com.example.myapplication.model.data.local.CharacterLocalDataSource
import com.example.myapplication.model.data.remote.CharacterRemoteDataSource
import com.example.myapplication.model.data.repository.CharacterRepositoryImpl
import com.example.myapplication.model.domain.repository.CharacterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCharacterRepository(
        local: CharacterLocalDataSource,
        remote: CharacterRemoteDataSource
    ): CharacterRepository = CharacterRepositoryImpl(local,remote)
}
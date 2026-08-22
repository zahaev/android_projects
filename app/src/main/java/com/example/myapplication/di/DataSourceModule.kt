package com.example.myapplication.di

import com.example.myapplication.model.data.local.CharacterLocalDataSource
import com.example.myapplication.model.data.local.dao.CharacterDao
import com.example.myapplication.model.data.remote.CharacterRemoteDataSource
import com.example.myapplication.model.data.remote.RickMortyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


//создаем оба DataSource
@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        api: RickMortyApi
    ): CharacterRemoteDataSource = CharacterRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideLocalDataSource(
        dao:CharacterDao
    ): CharacterLocalDataSource=CharacterLocalDataSource(dao)
}
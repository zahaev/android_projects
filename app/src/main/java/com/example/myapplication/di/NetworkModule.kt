package com.example.myapplication.di

import com.example.myapplication.BuildConfig
import com.example.myapplication.model.data.remote.RickMortyApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

//создание Retrofit
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun peovideMoshi(): Moshi= Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideRickMortyApi(retrofit: Retrofit):RickMortyApi=
        retrofit.create(RickMortyApi::class.java)
}
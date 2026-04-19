package com.example.myapplication.model.data.remote

import com.example.myapplication.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory



object RetrofitClient {

    private const val BASE_URL = BuildConfig.BASE_URL

    fun getClient(): Retrofit {
        // Moshi конвертер для преобразования JSON в Kotlin объекты
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        // Создаём и возвращаем Retrofit клиент
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
// Создаём интерфейс API

    val api: RickMortyApi = getClient().create(RickMortyApi::class.java)
}
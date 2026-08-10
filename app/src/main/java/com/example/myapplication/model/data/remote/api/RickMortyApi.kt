package com.example.myapplication.model.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface RickMortyApi {

    @GET("character")
    suspend fun getCharacters(
        @Query("page")page:Int,
        @Query("name")name:String?=null
    ): CharacterResponce
}
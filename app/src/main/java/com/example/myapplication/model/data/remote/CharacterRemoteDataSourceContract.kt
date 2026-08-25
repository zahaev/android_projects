package com.example.myapplication.model.data.remote


import com.example.myapplication.model.data.remote.dto.CharacterDto

interface CharacterRemoteDataSourceContract {

    suspend fun getCharacters(
        page: Int,
        name: String? = null,
        status: String? = null,
        species: String? = null,
        gender: String? = null
    ): List<CharacterDto>
}
package com.example.myapplication.model.data.local

import com.example.myapplication.model.data.local.entity.CharacterEntity

interface CharacterLocalDataSourceContract {

    suspend fun getCharactersPage(
        offset: Int,
        limit: Int
    ): List<CharacterEntity>

    suspend fun getCharacterById(
        id: Int
    ): CharacterEntity?

    suspend fun getFavorites(): List<CharacterEntity>

    suspend fun isFavorite(
        id: Int
    ): Boolean

    suspend fun updateFavoriteStatus(
        id: Int,
        isFavorite: Boolean
    )

    suspend fun insertAll(
        characters: List<CharacterEntity>
    )
}
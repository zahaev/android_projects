package com.example.myapplication.model.domain.repository

//import com.example.myapplication.model.domain.model.Character
import com.example.myapplication.model.domain.model.Character

//интерфейс
interface CharacterRepository {

    suspend fun getCharactersPage(page: Int, pageSize: Int): List<Character>

    suspend fun getCharacterById(id: Int): Character?

    suspend fun getFavorites(): List<Character>

    suspend fun toggleFavorite(characterId: Int)

    suspend fun isFavorite(id: Int): Boolean

    suspend fun addCharacter(character: Character)

    suspend fun deleteCharacter(id: Int)

}
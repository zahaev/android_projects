package com.example.myapplication.model.domain.repository
import com.example.myapplication.model.domain.Character
import com.example.myapplication.view.CharacterUi

//интерфейс
interface CharacterRepository {

    suspend fun getCharactersPage(page: Int, pageSize: Int): List<Character>

    suspend fun getCharactersPageUi(page: Int, pageSize: Int): List<CharacterUi>

    suspend fun getCharacterById(id: Int): Character?

    suspend fun getFavorites(): List<Character>

    suspend fun toggleFavorite(character: Character)

    suspend fun isFavorite(id: Int): Boolean

    suspend fun addCharacter(character: Character)

    suspend fun deleteCharacter(id: Int)

}
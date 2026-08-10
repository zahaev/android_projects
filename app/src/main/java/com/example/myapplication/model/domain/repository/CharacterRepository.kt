package com.example.myapplication.model.domain.repository

//import com.example.myapplication.model.domain.model.Character
import com.example.myapplication.model.domain.model.Character

//интерфейс
interface CharacterRepository {

    suspend fun getCharactersPage(page: Int, pageSize: Int): List<Character>
    /**
     * Получает страницу персонажей.
     * Реализация сама решает, брать из сети или кэша.
     */
    suspend fun searchCharacters(
    query: String,
    page: Int,
    pageSize: Int
    ):List<Character>
    /**
     * Поиск персонажей.
     */
    suspend fun getCharacterById(id: Int): Character?
    /**
     * Получает персонажа по ID (например, для экрана деталей)
     */
    suspend fun getFavorites(): List<Character>
    /**
     * Получает список избранных персонажей
     */
    suspend fun toggleFavorite(characterId: Int)
    /**
     * Переключает статус избранного
     */
    suspend fun isFavorite(id: Int): Boolean
}
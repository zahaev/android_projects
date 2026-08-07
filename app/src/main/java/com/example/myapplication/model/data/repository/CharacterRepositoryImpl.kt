// model/data/repository/CharacterRepositoryImpl.kt
package com.example.myapplication.model.data.repository

import android.util.Log

import com.example.myapplication.model.data.local.CharacterLocalDataSource
import com.example.myapplication.model.data.remote.CharacterRemoteDataSource
import com.example.myapplication.model.data.mapper.toDomain
import com.example.myapplication.model.data.mapper.mapCharacterDtoToEntity
import com.example.myapplication.model.data.mapper.toEntity
//import com.example.myapplication.model.data.remote.dto.CharacterDto
import com.example.myapplication.model.domain.model.Character

import com.example.myapplication.model.domain.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


//Спрашивает Local
//Если пусто → идёт в Remote
//Сохраняет в БД
//Возвращает Domain
class CharacterRepositoryImpl(
    private val localDataSource: CharacterLocalDataSource,
    private val remoteDataSource:CharacterRemoteDataSource
) : CharacterRepository {

    override suspend fun getCharactersPage(
        page: Int,
        pageSize: Int
    ): List<Character> = withContext(Dispatchers.IO) {
        try {
            // 1. Пытаемся получить свежие данные из сети
            val remoteDtos = remoteDataSource.getCharacters(page)

            // 2. Маппим DTO (Сеть) -> Entity (БД)
            val entities = remoteDtos.map { mapCharacterDtoToEntity(it) }

            // 3. Сохраняем в локальную БД (Room обновит существующие записи по id)
            localDataSource.insertAll(entities)

            // 4. Возвращаем Domain-модели из БД.
            // Это гарантирует, что мы получим актуальный флаг isFavorite,
            // который мог быть изменен пользователем ранее.
            val offset = (page - 1) * pageSize
            localDataSource.getCharactersPage(offset, pageSize).map { it.toDomain() }

        } catch (e: Exception) {
            // 5. FALLBACK: Если сети нет, тихо отдаем данные из локального кэша
            Log.e("CharacterRepository", "Network request failed, falling back to local cache", e)
            val offset = (page - 1) * pageSize
            localDataSource.getCharactersPage(offset, pageSize).map { it.toDomain() }
        }
    }
    override suspend fun getFavorites(): List<Character> = withContext(Dispatchers.IO) {
        localDataSource.getFavorites().map { it.toDomain() }
    }

    override suspend fun isFavorite(id: Int): Boolean = withContext(Dispatchers.IO) {
        localDataSource.isFavorite(id)
    }

    override suspend fun toggleFavorite(characterId: Int) = withContext(Dispatchers.IO) {
        val current = localDataSource.isFavorite(characterId)
            localDataSource.updateFavoriteStatus(characterId,!current)
    }


    override suspend fun getCharacterById(id: Int): Character? =
        withContext(Dispatchers.IO) {
            localDataSource.getCharacterById(id)?.toDomain()
        }

}
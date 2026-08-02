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
        val offset = (page-1) * pageSize
        val localData = localDataSource.getCharactersPage(offset, pageSize)
        if (localData.isEmpty()) {
            try {
                Log.d("Repo", "Loading from network, page=$page")

                val remoteData = remoteDataSource.getCharacters(page)

                val entities = remoteData.map { mapCharacterDtoToEntity(it) }//mapper CharacterDto -> CharacterEntity
                //Сохраняем в локальную БД.
                localDataSource.insertAll(entities)
            } catch (e: Exception) {
                Log.e("Repo", "Network load failed: ${e.message}", e)
            }
        }
        // Всегда возвращаем данные из локальной БД для запрошенного offset
       localDataSource.getCharactersPage(offset, pageSize)
           .map { it.toDomain() }
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
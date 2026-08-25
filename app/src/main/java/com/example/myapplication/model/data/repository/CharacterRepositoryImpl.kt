// model/data/repository/CharacterRepositoryImpl.kt
package com.example.myapplication.model.data.repository



import com.example.myapplication.model.domain.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import com.example.myapplication.model.data.local.CharacterLocalDataSourceContract
import com.example.myapplication.model.data.remote.CharacterRemoteDataSourceContract
import com.example.myapplication.model.data.mapper.toDomain
import com.example.myapplication.model.data.mapper.mapCharacterDtoToEntity
import com.example.myapplication.model.domain.model.Character


//Спрашивает Local
//Если пусто → идёт в Remote
//Сохраняет в БД
//Возвращает Domain
class CharacterRepositoryImpl(
    private val localDataSource: CharacterLocalDataSourceContract,
    private val remoteDataSource:CharacterRemoteDataSourceContract
) : CharacterRepository {

    override suspend fun getCharactersPage(
        page: Int,
        pageSize: Int,
        searchQuery: String,
        status: String?,
        species: String?,
        gender: String?
    ): List<Character> = withContext(Dispatchers.IO) {
        try {
            // Определяем, используется ли поиск или хотя бы один фильтр
            val hasFilters =
                searchQuery.isNotBlank() ||
                        !status.isNullOrBlank() ||
                        !species.isNullOrBlank() ||
                        !gender.isNullOrBlank()
            // 1. Пытаемся получить свежие данные из сети
            val remoteDtos = remoteDataSource.getCharacters(
                page = page,
                name = searchQuery
                    .trim()
                    .takeIf { it.isNotBlank() },

                status = status
                    ?.takeIf { it.isNotBlank() },

                species = species
                    ?.takeIf { it.isNotBlank() },

                gender = gender
                    ?.takeIf { it.isNotBlank() })

            // 2. Маппим DTO (Сеть) -> Entity (БД)
            val entities = remoteDtos.map { mapCharacterDtoToEntity(it) }

            // 3. Сохраняем в локальную БД (Room обновит существующие записи по id)
            localDataSource.insertAll(entities)
            /*
                * Если используется поиск или фильтр,
                * возвращаем именно результаты API.
                *
                * Нельзя брать здесь данные через
                * getCharactersPage() из Room,
                * потому что Room не знает о фильтрах API.
                */
            if (hasFilters) {

                return@withContext entities.map { it.toDomain() }
            }

            /*
            * Обычный список без поиска и фильтров.
            * Здесь используем Room.
            */
            val offset = (page - 1) * pageSize
            return@withContext localDataSource
                .getCharactersPage(offset, pageSize).map { it.toDomain() }

        } catch (e: Exception) {
            //  Если сети нет,отдаем данные из локального кэша
            /*
            * если это запрос с фильтрами или поиском
            * нельзя отдавать случайные данные из Room
            * поэтому для фильтрованного запроса
            * возвращаем ошибку дальше во ViewModel
            */
            /*
       * При поиске/фильтрах нельзя показывать
       * случайные данные из Room.
       */
            if (
                searchQuery.isNotBlank() ||
                !status.isNullOrBlank() ||
                !species.isNullOrBlank() ||
                !gender.isNullOrBlank()
            ) {
                throw e
            }

            /*
             * Для обычного списка без фильтров
             * оставляем fallback на Room.
             */
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
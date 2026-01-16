// com/example/myapplication/model/repository/CharacterRepository.kt
package com.example.myapplication.model.repository

import android.util.Log
import android.content.Context
import com.example.myapplication.model.domain.Character
import com.example.myapplication.model.local.AppDatabase
import com.example.myapplication.model.local.CharacterEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData

object CharacterRepository {

    private lateinit var database: AppDatabase


    fun initialize(context: Context) {
        database = AppDatabase.getDatabase(context)
    }
    // Получение списка персонажей (из БД)
    suspend fun getCharacters(): List<Character> {
        return withContext(Dispatchers.IO) {
            val entities = database.characterDao().getAllCharacters()
            entities.map { entity ->
                Character(
                    id = entity.id,
                    name = entity.name,
                    age = entity.age,
                    imageUrl = entity.imageUrl,
                    description = entity.description
                )
            }
        }
    }
    suspend fun getCharactersPage(page: Int, pageSize: Int = 5): List<Character> {
        return withContext(Dispatchers.IO) {
            val offset = page * pageSize
            database.characterDao()
                .getCharactersPage(offset, pageSize)
                .map { entity ->
                    Character(
                        id = entity.id,
                        name = entity.name,
                        age = entity.age,
                        imageUrl = entity.imageUrl,
                        description = entity.description
                    )
                }
        }
    }

    // Получение одного персонажа по ID (из БД)
    suspend fun getCharacterById(id: Int): Character? {
        return withContext(Dispatchers.IO) {
            val entity = database.characterDao().getCharacterById(id)
            entity?.let {
                Character(
                    id = it.id,
                    name = it.name,
                    age = it.age,
                    imageUrl = it.imageUrl,
                    description = it.description
                )
            }
        }
    }
    suspend fun insertCharacter(character: Character){
        withContext(Dispatchers.IO){
            val entity = CharacterEntity(
                id=0, //автоинкремент room
                name = character.name,
                age = character.age,
                imageUrl = character.imageUrl,
                description = character.description
            )
            database.characterDao().insertCharacter(entity)
        }
    }
    suspend fun deleteCharacter(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                database.characterDao().deleteCharacter(id) // ← Передаём ID напрямую
            } catch (e: Exception) {
                Log.e("CharacterRepository", "Failed to delete character $id", e)
                throw e
            }
        }
    }
}

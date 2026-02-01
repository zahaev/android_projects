package com.example.myapplication.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getCharactersPage(offset: Int, limit: Int): List<CharacterEntity>

    @Query("SELECT * FROM characters ORDER BY id ASC")
    suspend fun getAllCharacters(): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: CharacterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteCharacter(id: Int)

    @Query("DELETE FROM characters")
    suspend fun deleteAll()
}

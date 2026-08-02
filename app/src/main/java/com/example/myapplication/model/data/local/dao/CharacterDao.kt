package com.example.myapplication.model.data.local.dao
// com/example/myapplication/model/local/CharacterDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.myapplication.model.data.local.entity.CharacterEntity

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getCharactersPage(offset: Int, limit: Int): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Query("SELECT * FROM characters WHERE isFavorite = 1 ORDER BY id ASC")
    suspend fun getFavorites(): List<CharacterEntity>

    @Query("SELECT isFavorite FROM characters WHERE id = :id LIMIT 1")
    suspend fun isFavorite(id: Int): Boolean?

    @Query("UPDATE characters SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

}

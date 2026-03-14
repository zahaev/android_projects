package com.example.myapplication.model.data.local
// com/example/myapplication/model/local/CharacterDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getCharactersPage(offset: Int, limit: Int): List<CharacterEntity>

    @Query("SELECT * FROM characters ORDER BY id ASC")
    suspend fun getAllCharacters(): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Query("SELECT * FROM favorites ORDER BY id ASC")
    suspend fun getAllFavorites(): List<FavoriteCharacterEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(character:FavoriteCharacterEntity)

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteCharacter(id: Int)

    @Query("DELETE FROM characters")
    suspend fun deleteAll()
    @Query("DELETE FROM favorites WHERE id =:id")

    suspend fun  deleteFavorite(id:Int)
    @Transaction
    @Query("""
    SELECT * FROM characters
    ORDER BY id ASC
    LIMIT :limit OFFSET :offset
""")

    suspend fun getCharactersPageWithFavorite(
        offset: Int,
        limit: Int
    ): List<CharacterWithFavorite>
}

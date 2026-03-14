package com.example.myapplication.model.data.local
import com.example.myapplication.model.data.local.CharacterDao
//возвращает Entity
//вызывает DAO
class CharacterLocalDataSource(
    private val dao: CharacterDao
) {
    suspend fun getCharactersPage(offset: Int, limit: Int): List<CharacterEntity> {
        return dao.getCharactersPage(offset, limit)
    }

    suspend fun getCharacterById(id: Int): CharacterEntity? {
        return dao.getCharacterById(id)
    }
    suspend fun getAllFavorites() = dao.getAllFavorites()
    suspend fun getCharactersPageWithFavorite(offset: Int, limit: Int): List<CharacterWithFavorite> {
        return dao.getCharactersPageWithFavorite(offset, limit)
    }
    suspend fun isFavorite(id: Int) = dao.isFavorite(id)
    suspend fun insertFavorite(entity: FavoriteCharacterEntity) = dao.insertFavorite(entity)
    suspend fun deleteFavorite(id: Int) = dao.deleteFavorite(id)

    suspend fun insert(character: CharacterEntity) {
        dao.insertCharacter(character)
    }
    suspend fun insertAll(characters: List<CharacterEntity>) {
        dao.insertAll(characters)
    }

    suspend fun delete(id: Int) {
        dao.deleteCharacter(id)
    }
}
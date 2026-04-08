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
    suspend fun getFavorites() = dao.getFavorites()

    suspend fun isFavorite(id: Int) = dao.isFavorite(id) ?:false


    suspend fun updateFavoriteStatus(id:Int,isFavorite:Boolean)=
        dao.updateFavoriteStatus(id, isFavorite)
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
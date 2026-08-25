package com.example.myapplication.model.data.local
import com.example.myapplication.model.data.local.dao.CharacterDao
import com.example.myapplication.model.data.local.entity.CharacterEntity

//возвращает Entity
//вызывает DAO
class CharacterLocalDataSource(
    private val dao: CharacterDao
): CharacterLocalDataSourceContract {
    override suspend fun getCharactersPage(offset: Int, limit: Int): List<CharacterEntity> {
        return dao.getCharactersPage(offset, limit)
    }

    override suspend fun getCharacterById(id: Int): CharacterEntity? {
        return dao.getCharacterById(id)
    }
    override suspend fun getFavorites() = dao.getFavorites()

    override suspend fun isFavorite(id: Int) = dao.isFavorite(id) ?:false


    override suspend fun updateFavoriteStatus(id:Int,isFavorite:Boolean)=
        dao.updateFavoriteStatus(id, isFavorite)

    override suspend fun insertAll(characters: List<CharacterEntity>) {
        dao.insertAll(characters)
    }


}
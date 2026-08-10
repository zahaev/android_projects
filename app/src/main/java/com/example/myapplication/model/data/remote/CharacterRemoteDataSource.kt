package com.example.myapplication.model.data.remote
//вызывает API
//возвращает DTO
import com.example.myapplication.model.data.remote.dto.CharacterDto

class CharacterRemoteDataSource(
    private val api: RickMortyApi
) {

    suspend fun getCharacters(page:Int): List<CharacterDto> {
        return api.getCharacters(page).results// Отправляем запрос и возвращаем список персонажей

    }
    suspend fun searchCharacters(
        query:String,
        page: Int
    ):List<CharacterDto>{
        return  api.getCharacters(
                page=page,
                name = query
                ).results
    }
}
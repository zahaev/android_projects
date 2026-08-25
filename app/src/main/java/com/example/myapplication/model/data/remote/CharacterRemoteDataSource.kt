package com.example.myapplication.model.data.remote
//вызывает API
//возвращает DTO
import com.example.myapplication.model.data.remote.dto.CharacterDto

class CharacterRemoteDataSource(
    private val api: RickMortyApi
) : CharacterRemoteDataSourceContract {

    override suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
        species: String?,
        gender: String?
    ): List<CharacterDto> {
        return api.getCharacters(
            page = page,
            name = name,
            status = status,
            species = species,
            gender = gender
        ).results// Отправляем запрос и возвращаем список персонажей

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
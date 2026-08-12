package com.example.myapplication.model.data.remote
//вызывает API
//возвращает DTO
import androidx.core.app.GrammaticalInflectionManagerCompat.GrammaticalGender
import androidx.core.app.GrammaticalInflectionManagerCompat.getApplicationGrammaticalGender
import com.example.myapplication.model.data.remote.dto.CharacterDto

class CharacterRemoteDataSource(
    private val api: RickMortyApi
) {

    suspend fun getCharacters(
        page:Int,
        name:String? = null,
        status:String? = null,
        species: String? = null,
        gender: String? = null
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
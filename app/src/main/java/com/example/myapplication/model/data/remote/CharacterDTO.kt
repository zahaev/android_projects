package com.example.myapplication.model.data.remote
import com.squareup.moshi.Json
//модель сети Связана с JSON
//формат json для работы с API
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: LocationDto,
    val location: LocationDto,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
)

data class LocationDto(
    val name: String,
    val url: String
)
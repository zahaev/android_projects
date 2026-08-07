package com.example.myapplication.model.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
//модель сети Связана с JSON
//формат json для работы с API
@JsonClass(generateAdapter = true)
data class CharacterDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "status") val status: String,
    @Json(name = "species") val species: String,
    @Json(name = "type") val type: String,
    @Json(name = "gender") val gender: String,
    @Json(name = "origin") val origin: LocationDto?,
    @Json(name = "location") val location: LocationDto?,
    @Json(name = "image") val image: String,
    @Json(name = "episode") val episode: List<String>,
    @Json(name = "url") val url: String,
    @Json(name = "created") val created: String
)

@JsonClass(generateAdapter = true)
data class LocationDto(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)
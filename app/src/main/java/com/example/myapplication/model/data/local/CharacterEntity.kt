// com/example/myapplication/model/local/CharacterEntity.kt
package com.example.myapplication.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
//Это модель базы данных Связана с Room.
@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,          // id из API, НЕ autoGenerate
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: ApiLocation,          // через Moshi-конвертер
    val location: ApiLocation,        // через Moshi-конвертер
    val image: String,
    val episode: List<String>,        // через Moshi-конвертер
    val firstEpisodeName: String?,    // nullable
    val url: String,
    val created: String
)

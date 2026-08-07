// com/example/myapplication/model/local/CharacterEntity.kt
package com.example.myapplication.model.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.model.data.local.RoomConverters
//Это модель базы данных Связана с Room.
@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,          // id из API, НЕ autoGenerate
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val originName: String,
    val originUrl: String,// через Moshi-конвертер
    val locationName: String, // через Moshi-конвертер
    val locationUrl: String,
    val image: String,
    val episode: String,        // через Moshi-конвертер храним как строку, разделенную запятыми
    val url: String,
    val created: String,
    val isFavorite: Boolean = false
)


package com.example.myapplication.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteCharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: ApiLocation,
    val location: ApiLocation,
    val image: String,
    val episode: List<String>,
    val firstEpisodeName: String?,
    val url: String,
    val created: String
)
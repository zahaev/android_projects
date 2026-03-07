package com.example.myapplication.model.data.local
import androidx.room.Embedded
import androidx.room.Relation

data class CharacterWithFavorite(
    @Embedded val character: CharacterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = FavoriteCharacterEntity::class
    )
    val favorites: List<FavoriteCharacterEntity>?
)

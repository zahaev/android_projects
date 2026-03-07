package com.example.myapplication.model.data.mapper

import com.example.myapplication.model.data.local.FavoriteCharacterEntity
import com.example.myapplication.model.domain.Character

fun Character.toFavoriteEntity(): FavoriteCharacterEntity =
    FavoriteCharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = origin,
        location = location,
        image = image,
        episode = episode,
        firstEpisodeName = firstEpisodeName,
        url = url,
        created = created
    )

fun FavoriteCharacterEntity.toDomain(): Character =
    Character(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = origin,
        location = location,
        image = image,
        episode = episode,
        firstEpisodeName = firstEpisodeName,
        url = url,
        created = created
    )
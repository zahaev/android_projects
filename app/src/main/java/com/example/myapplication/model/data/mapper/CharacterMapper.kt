package com.example.myapplication.model.data.mapper

import com.example.myapplication.model.domain.Character
import com.example.myapplication.model.data.local.CharacterEntity
import com.example.myapplication.model.data.local.ApiLocation
import com.example.myapplication.model.data.remote.CharacterDto

fun CharacterEntity.toDomain(): Character =
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
        url = url,
        created = created,
        isFavorite = isFavorite
    )

fun mapCharacterDtoToEntity(dto: CharacterDto): CharacterEntity =
    CharacterEntity(
        id = dto.id,
        name = dto.name,
        status = dto.status,
        species = dto.species,
        type = dto.type,
        gender = dto.gender,
        origin = ApiLocation(dto.origin.name, dto.origin.url),
        location = ApiLocation(dto.location.name, dto.location.url),
        image = dto.image,
        episode = dto.episode,
        firstEpisodeName = null,
        url = dto.url,
        created = dto.created,
        isFavorite = false
    )

fun Character.toEntity(): CharacterEntity =
    CharacterEntity(
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
        firstEpisodeName = null,
        url = url,
        created = created,
        isFavorite = isFavorite
    )
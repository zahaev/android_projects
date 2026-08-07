package com.example.myapplication.model.data.mapper

import com.example.myapplication.model.domain.model.Character
import com.example.myapplication.model.data.local.entity.CharacterEntity

import com.example.myapplication.model.data.remote.dto.CharacterDto
import com.example.myapplication.model.domain.model.Location

fun CharacterEntity.toDomain(): Character =
    Character(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,

        origin = Location(

            name= originName,
            url=originUrl
        ),

        location = Location(

            name = locationName,
            url = locationUrl

        ),

        image = image,
        episode = if (episode.isBlank())
            emptyList()
        else
            episode.split(","),
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

        originName = dto.origin?.name?:"",
        originUrl =  dto.origin?.url ?:"",

        locationName = dto.location?.name?:"",
        locationUrl = dto.location?.url ?:"",

        image = dto.image,

        episode = dto.episode.joinToString(","),

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

        originName = origin.name,
        originUrl = origin.url,

        locationName = location.name,
        locationUrl = location.url,

        image = image,
        episode = episode.joinToString(","),
        url = url,
        created = created,
        isFavorite = isFavorite
    )
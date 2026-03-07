package com.example.myapplication.model.data.mapper

import com.example.myapplication.model.domain.Character
import com.example.myapplication.model.data.local.CharacterEntity
import com.example.myapplication.model.data.local.ApiLocation
// разделение слоев DTO и Domain
// конвертация БД (DTO) в json и обратно
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
        firstEpisodeName = firstEpisodeName,
        url = url,
        created = created
    )
fun mapCharacterDtoToEntity(dto: com.example.myapplication.model.data.remote.CharacterDto): CharacterEntity =
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
        created = dto.created
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
        firstEpisodeName = firstEpisodeName,
        url = url,
        created = created
    )
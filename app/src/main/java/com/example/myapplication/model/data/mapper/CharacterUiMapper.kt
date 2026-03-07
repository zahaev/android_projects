package com.example.myapplication.model.data.mapper

import com.example.myapplication.model.data.local.CharacterWithFavorite
import com.example.myapplication.model.domain.CharacterUi
import com.example.myapplication.model.data.mapper.toDomain

fun CharacterWithFavorite.toUi(): CharacterUi =
    CharacterUi(
        character = character.toDomain(),
        isFavorite = favorites?.isNotEmpty() == true
    )
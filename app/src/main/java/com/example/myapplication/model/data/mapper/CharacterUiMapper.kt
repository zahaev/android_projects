package com.example.myapplication.model.data.mapper

import com.example.myapplication.model.data.local.CharacterWithFavorite
import com.example.myapplication.view.CharacterUi

fun CharacterWithFavorite.toUi(): CharacterUi =
    CharacterUi(
        character = character.toDomain(),
        isFavorite = favorites?.isNotEmpty() == true
    )
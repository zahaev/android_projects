package com.example.myapplication.model.domain

import com.example.myapplication.model.data.local.ApiLocation

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: ApiLocation,
    val location: ApiLocation,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
)

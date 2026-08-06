package com.example.myapplication.viewmodel
import com.example.myapplication.model.domain.model.Character
data class CharactersUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val endReached: Boolean = false
)

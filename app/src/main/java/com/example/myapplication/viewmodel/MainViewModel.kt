package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.domain.Character
import com.example.myapplication.model.data.local.ApiLocation
import com.example.myapplication.model.domain.repository.CharacterRepository
import com.example.myapplication.view.CharacterUi
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _charactersUi = MutableLiveData<List<CharacterUi>>()
    val charactersUi: LiveData<List<CharacterUi>> = _charactersUi

    private var currentPage = 0
    private var isLastPage = false
    private var isLoading = false

    fun loadFirstPage() {
        currentPage = 0
        isLastPage = false
        isLoading = false
        _charactersUi.value = emptyList()  // ✅ Исправлено: charactersUi
        loadNextPage()
    }

    suspend fun isFavoriteSync(id: Int): Boolean {
        return repository.isFavorite(id)
    }

    fun toggleFavorite(character: Character) {
        viewModelScope.launch {
            repository.toggleFavorite(character)
            // Обновляем список после изменения избранного
            loadFirstPage()
        }
    }

    fun loadNextPage(onComplete: () -> Unit = {}) {
        if (isLoading || isLastPage) {
            onComplete()
            return
        }
        isLoading = true
        viewModelScope.launch {
            try {
                // ✅ Исправлено: используем getCharactersPageUi (возвращает CharacterUi)
                val newChars = repository.getCharactersPageUi(currentPage, 5)

                if (newChars.isEmpty()) {
                    isLastPage = true
                } else {
                    val currentList = _charactersUi.value.orEmpty().toMutableList()
                    val existingIds = currentList.map { it.character.id }.toSet()  // ✅ .character.id
                    val uniqueNewChars = newChars.filter { it.character.id !in existingIds }
                    currentList.addAll(uniqueNewChars)
                    _charactersUi.value = currentList  // ✅ Исправлено: charactersUi
                    currentPage++
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to load page", e)
            } finally {
                isLoading = false
                onComplete()
            }
        }
    }

    fun addCharacter(
        name: String,
        status: String,
        species: String,
        gender: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            try {
                val newChar = Character(
                    id = 0,
                    name = name,
                    status = status,
                    species = species,
                    type = "unknown",
                    gender = gender,
                    origin = ApiLocation("unknown", ""),
                    location = ApiLocation("unknown", ""),
                    image = imageUrl,
                    episode = emptyList(),
                    url = "",
                    created = ""
                )
                repository.addCharacter(newChar)
                loadFirstPage()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to add character", e)
            }
        }
    }

    fun deleteCharacter(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteCharacter(id)
                loadFirstPage()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to delete character $id", e)
            }
        }
    }
}
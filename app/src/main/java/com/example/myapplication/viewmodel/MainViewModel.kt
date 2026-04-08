package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.domain.model.Character
import com.example.myapplication.model.data.local.ApiLocation
import com.example.myapplication.model.domain.repository.CharacterRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _characters = MutableLiveData<List<Character>>()
    val characters: LiveData<List<Character>> = _characters

    private var currentPage = 0
    private var isLastPage = false
    private var isLoading = false

    fun loadFirstPage() {
        currentPage = 0
        isLastPage = false
        isLoading = false
        _characters.value = emptyList()
        loadNextPage()
    }

    suspend fun isFavoriteSync(id: Int): Boolean {
        return repository.isFavorite(id)
    }

    fun toggleFavorite(characterId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(characterId)
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
                //  используем getCharactersPage (возвращает Character)
                val newChars = repository.getCharactersPage(currentPage, 5)

                if (newChars.isEmpty()) {
                    isLastPage = true
                } else {
                    val currentList = _characters.value.orEmpty().toMutableList()
                    val existingIds = currentList.map { it.id }.toSet()  //characterId
                    val uniqueNewChars = newChars.filter { it.id !in existingIds }//characterId
                    currentList.addAll(uniqueNewChars)
                    _characters.value = currentList
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
        imageUrl: String,
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
                    created = "",
                    isFavorite = false
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
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

    private var currentPage = 1
    private var isEndReached = false
    private var isLoading = false


    fun loadFirstPage() {
        currentPage = 1
        isEndReached = false
        isLoading = false
        _characters.value = emptyList() // Очистка UI перед новой загрузкой
        loadNextPage()
    }

    fun toggleFavorite(characterId: Int) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(characterId)
                // Обновляем список чтобы изменить иконку избранного
                val currentList = _characters.value.orEmpty().map {char ->
                    if(char.id==characterId) char.copy(isFavorite = !char.isFavorite)
                    else char
                }
                _characters.value=currentList
            }
            catch (e: Exception){
                Log.e("MaintViewModel","Failed to toggle favorite",e)
            }
        }
    }

    fun loadNextPage(onComplete: () -> Unit = {}) {
        if (isLoading || isEndReached) {
            onComplete()
            return
        }
        isLoading = true
        viewModelScope.launch {
            try {
                //  используем getCharactersPage (возвращает Character)
                val newChars = repository.getCharactersPage(currentPage, 20)

                if (newChars.isEmpty()) {
                    isEndReached = true
                } else {
                    val currentList = _characters.value.orEmpty().toMutableList()
                    val existingIds = currentList.map { it.id }.toSet()  //characterId
                    // Фильтруем дубликаты
                    val uniqueNewChars = newChars.filter { it.id !in existingIds }//characterId
                    currentList.addAll(uniqueNewChars)
                    _characters.value = currentList
                    currentPage++// Переход к следующей странице
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
                // Генерируем отрицательный ID, чтобы избежать конфликтов с ID из API (которые > 0)
                val localId = -(System.currentTimeMillis() % Int.MAX_VALUE).toInt()

                val newChar = Character(
                    id = localId,
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
                val currentList = _characters.value.orEmpty().toMutableList()
                currentList.removeAll { it.id == id }
                _characters.value = currentList
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to delete character $id", e)
            }
        }
    }
}
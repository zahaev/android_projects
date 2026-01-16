// com/example/myapplication/viewmodel/MainViewModel.kt
package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.domain.Character
import com.example.myapplication.model.repository.CharacterRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _characters = MutableLiveData<List<Character>>()
    val characters: LiveData<List<Character>> = _characters

    private var currentPage = 0
    private var isLastPage = false

    fun loadFirstPage() {
        currentPage = 0
        isLastPage = false
        _characters.value = emptyList()
        loadNextPage {}
    }

    fun loadNextPage(onComplete: () -> Unit = {}) {
        if (isLastPage) {
            onComplete()
            return
        }

        viewModelScope.launch {
            try {
                val newChars = CharacterRepository.getCharactersPage(currentPage, 5)
                if (newChars.isEmpty()) {
                    isLastPage = true
                } else {
                    val currentList = _characters.value.orEmpty().toMutableList()
                    currentList.addAll(newChars)
                    _characters.value = currentList
                    currentPage++
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to load page", e)
            } finally {
                onComplete()
            }
        }
    }

    fun addCharacter(name: String, age: String, imageUrl: String, description: String) {
        viewModelScope.launch {
            try {
                val newChar = com.example.myapplication.model.domain.Character(
                    id = 0,
                    name = name,
                    age = age,
                    imageUrl = imageUrl,
                    description = description
                )
                CharacterRepository.insertCharacter(newChar)
                // После добавления перезагружаем первую страницу
                loadFirstPage()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to add character", e)
            }
        }
    }

    fun deleteCharacter(id: Int) {
        viewModelScope.launch {
            try {
                CharacterRepository.deleteCharacter(id)
                // После удаления перезагружаем первую страницу
                loadFirstPage()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to delete character $id", e)
            }
        }
    }
}

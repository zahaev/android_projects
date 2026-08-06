package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.domain.model.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.myapplication.model.data.local.ApiLocation
import com.example.myapplication.model.domain.repository.CharacterRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    private var currentPage = 1

    fun loadFirstPage() {
        currentPage = 1
        _uiState.update {it.copy(
            characters = emptyList(), // Очистка UI перед новой загрузкой
            isLoading = true,
            errorMessage = null,
            endReached = false
            )
        }
        loadNextPage()
    }

    fun toggleFavorite(characterId: Int) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(characterId)
                // Обновляем список чтобы изменить иконку избранного без перезагрузки всей страницы
                _uiState.update { currentState->
                    val updatedList = currentState.characters.map{char ->
                        if(char.id==characterId) char.copy(isFavorite = !char.isFavorite)
                        else char
                    }
                    currentState.copy(characters = updatedList)
                }
            }
            catch (e: Exception){
                Log.e("MainViewModel","Failed to toggle favorite",e)
            }
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.endReached){
            return
        }
            _uiState.update { it.copy(isLoading = true, errorMessage = null)
            }
        viewModelScope.launch {
            try {
                //  используем getCharactersPage (возвращает Character)
                val newChars = repository.getCharactersPage(currentPage, 20)

                if (newChars.isEmpty()) {
                  _uiState.update { it.copy(isLoading = false, endReached = true) }
                } else {
                    val existingIds = currentState.characters.map { it.id }.toSet()  //characterId
                    // Фильтруем дубликаты
                    val uniqueNewChars = newChars.filter { it.id !in existingIds }//characterId
                    _uiState.update {
                        it.copy(
                            characters = currentState.characters + uniqueNewChars,
                            isLoading = false,
                            endReached = false
                        )
                    }
                    currentPage++// Переход к следующей странице
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to load page $currentPage", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Ошибка загрузки")
                }
            }
            }
        }
    }
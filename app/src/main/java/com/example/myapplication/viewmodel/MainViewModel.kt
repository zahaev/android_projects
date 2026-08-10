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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CharacterRepository
) : ViewModel() {
    private var searchJob:Job? = null
    private val _uiState = MutableStateFlow(
        CharactersUiState())
    val uiState: StateFlow<CharactersUiState> =
        _uiState.asStateFlow()

    private var currentPage = 1

    fun onSearchQueryChange(query:String){

        _uiState.update {
            it.copy(
                searchQuery = query,
                errorMessage = null
            )
        }
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(300)
            currentPage=1

            _uiState.update {
                it.copy(
                    characters = emptyList(),
                    isLoading = false,
                    errorMessage = null,
                    endReached = false
                )
            }
            loadNextPage()
        }
    }
    fun loadFirstPage() {
        currentPage = 1
        _uiState.update {it.copy(
            characters = emptyList(), // Очистка UI перед новой загрузкой
            isLoading = false,//нет прогрузки
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
        val state = _uiState.value
            //если прогружается или жостигнут конец списка
            // то ничего не происходит
        if (state.isLoading || state.endReached){
            return
        }
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
                )
            }
        viewModelScope.launch {
            try {
                //Получаем поисковой запрос
                val query= _uiState.value.searchQuery.trim()
                /*
              * Если строка поиска пустая —
              * обычная загрузка персонажей.
              *
              * Если строка заполнена —
              * поиск через API.
              */
                val newChars = if(query.isBlank()){
                    repository.getCharactersPage(
                     page = currentPage,
                        20
                    )
                }else{
                    repository.searchCharacters(
                        query = query,
                        page = currentPage,
                        pageSize = 20
                    )
                }
                if (newChars.isEmpty()) {
                  _uiState.update {
                      it.copy(
                          isLoading = false,
                          endReached = true
                      ) }
                } else {
                    _uiState.update { currentState->
                        val existingIds = currentState.characters
                            .map { it.id }
                            .toSet()  //characterId
                        // Фильтруем дубликаты
                        val uniqueNewChars =
                            newChars.filter {
                                it.id !in existingIds
                            }//characterId
                        currentState.copy(
                            characters =
                                currentState.characters + uniqueNewChars,
                            isLoading = false,
                            endReached = false
                        )
                    }
                    currentPage++// Переход к следующей странице
                }
            } catch (e: Exception) {
                Log.e("MainViewModel",
                    "Failed to load page $currentPage", e)
                _uiState.update {
                    it.copy(isLoading = false,
                        errorMessage = e.message ?: "Ошибка загрузки")
                }
            }
        }
    }
}
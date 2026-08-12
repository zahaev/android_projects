package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.domain.repository.CharacterRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CharactersUiState()
    )

    val uiState: StateFlow<CharactersUiState> =
        _uiState.asStateFlow()

    // Текущая страница API
    private var currentPage = 1

    // Job поиска с debounce
    private var searchJob: Job? = null

    // Job загрузки страницы
    private var loadJob: Job? = null

    // Размер страницы
    private val pageSize = 20


    // ============================================================
    // ПЕРВИЧНАЯ ЗАГРУЗКА
    // ============================================================

    fun loadFirstPage() {

        currentPage = 1

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


    // ============================================================
    // ПОИСК
    // ============================================================

    fun onSearchQueryChange(query: String) {

        // Сохраняем текст поиска
        _uiState.update {
            it.copy(
                searchQuery = query,
                errorMessage = null
            )
        }

        // Отменяем предыдущий отложенный поиск
        searchJob?.cancel()

        searchJob = viewModelScope.launch {

            // Небольшая задержка,
            // чтобы не отправлять запрос на каждый символ
            delay(300)

            restartFromFirstPage()
        }
    }


    // ============================================================
    // ФИЛЬТР STATUS
    // ============================================================

    fun onStatusChange(status: String?) {

        _uiState.update {
            it.copy(
                selectedStatus = status,
                errorMessage = null
            )
        }

        restartFromFirstPage()
    }


    // ============================================================
    // ФИЛЬТР SPECIES
    // ============================================================

    fun onSpeciesChange(species: String?) {

        _uiState.update {
            it.copy(
                selectedSpecies = species,
                errorMessage = null
            )
        }

        restartFromFirstPage()
    }


    // ============================================================
    // ФИЛЬТР GENDER
    // ============================================================

    fun onGenderChange(gender: String?) {

        _uiState.update {
            it.copy(
                selectedGender = gender,
                errorMessage = null
            )
        }

        restartFromFirstPage()
    }


    // ============================================================
    // СБРОС ФИЛЬТРОВ
    // ============================================================

    fun resetFilters() {

        searchJob?.cancel()

        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedStatus = null,
                selectedSpecies = null,
                selectedGender = null,

                characters = emptyList(),

                isLoading = false,
                errorMessage = null,
                endReached = false
            )
        }

        currentPage = 1

        loadNextPage()
    }


    // ============================================================
    // ПЕРЕЗАПУСК С ПЕРВОЙ СТРАНИЦЫ
    // ============================================================

    private fun restartFromFirstPage() {

        // Отменяем предыдущую загрузку
        loadJob?.cancel()

        currentPage = 1

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


    // ============================================================
    // ЗАГРУЗКА СЛЕДУЮЩЕЙ СТРАНИЦЫ
    // ============================================================

    fun loadNextPage() {

        val state = _uiState.value

        // Если уже идёт загрузка —
        // новый запрос не отправляем
        if (state.isLoading) {
            return
        }

        // Если API сообщил, что страниц больше нет
        if (state.endReached) {
            return
        }

        // Запоминаем параметры именно этого запроса.
        // Это важно, если пользователь быстро меняет фильтр.
        val page = currentPage
        val query = state.searchQuery.trim()

        val status = state.selectedStatus
        val species = state.selectedSpecies
        val gender = state.selectedGender

        // Показываем loading
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        loadJob = viewModelScope.launch {

            try {

                /*
                 * ВАЖНО:
                 *
                 * Теперь поиск и фильтры идут
                 * через ОДИН метод Repository.
                 *
                 * Никакого отдельного searchCharacters()
                 * больше не нужно.
                 */

                val newCharacters =
                    repository.getCharactersPage(
                        page = page,
                        pageSize = pageSize,
                        searchQuery = query,
                        status = status,
                        species = species,
                        gender = gender
                    )

                // API вернул пустую страницу
                if (newCharacters.isEmpty()) {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            endReached = true
                        )
                    }

                    return@launch
                }


                _uiState.update { currentState ->

                    // ID уже отображаемых персонажей
                    val existingIds =
                        currentState.characters
                            .map { it.id }
                            .toSet()

                    // Убираем возможные дубликаты
                    val uniqueCharacters =
                        newCharacters.filter { character ->
                            character.id !in existingIds
                        }

                    currentState.copy(
                        characters =
                            currentState.characters +
                                    uniqueCharacters,

                        isLoading = false,

                        endReached = false
                    )
                }

                // Переходим к следующей странице
                currentPage++

            } catch (e: Exception) {

                Log.e(
                    "MainViewModel",
                    "Failed to load page $page",
                    e
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            e.message ?: "Ошибка загрузки"
                    )
                }
            }
        }
    }


    // ============================================================
    // ИЗБРАННОЕ
    // ============================================================

    fun toggleFavorite(characterId: Int) {

        viewModelScope.launch {

            try {

                repository.toggleFavorite(characterId)

                _uiState.update { currentState ->

                    val updatedList =
                        currentState.characters.map { character ->

                            if (character.id == characterId) {

                                character.copy(
                                    isFavorite =
                                        !character.isFavorite
                                )

                            } else {
                                character
                            }
                        }

                    currentState.copy(
                        characters = updatedList
                    )
                }

            } catch (e: Exception) {

                Log.e(
                    "MainViewModel",
                    "Failed to toggle favorite",
                    e
                )
            }
        }
    }
}
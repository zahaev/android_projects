package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.domain.model.Character
import com.example.myapplication.model.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.myapplication.viewmodel.FavoritesUiState
import java.security.spec.ECField
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository:CharacterRepository
):ViewModel() {
    private val _uiState = MutableStateFlow(FavoritesUiState())

    val uiState: StateFlow<FavoritesUiState> =_uiState.asStateFlow()

    fun loadFavorites(){

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage=null
                )
            try {
                val favorites= repository.getFavorites()
                _uiState.value =_uiState.value.copy(
                    characters = favorites,
                    isLoading = false
                )
            }catch (e:Exception){
                Log.e("FavoritesViewModel",
                    "Failed to load favorites",e)
                _uiState.value= _uiState.value.copy(
                    isLoading = false,
                    errorMessage =
                        e.message?:"Ошибка загрузки избранного"
                )
            }
        }
    }
    fun toggleFavorite(characterId:Int){
        viewModelScope.launch {
            try {
                repository.toggleFavorite(characterId)
                loadFavorites()
            }catch (e:Exception){

                Log.e(
                    "FavoritesViewModel,",
                            "Failed to toggle favorite",e)
            }
        }
    }
}
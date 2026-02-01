package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.domain.Character
import com.example.myapplication.model.repository.CharacterRepository
import kotlinx.coroutines.launch

class CharacterDetailViewModel : ViewModel() {

    private val _character = MutableLiveData<Character?>()
    val character: LiveData<Character?> = _character

    fun loadCharacter(id: Int) {
        viewModelScope.launch {
            try {
                val char = CharacterRepository.getCharacterById(id)
                _character.value = char
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Failed to load character $id", e)
                _character.value = null
            }
        }
    }
}

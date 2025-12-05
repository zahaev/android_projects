package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Character
import com.example.myapplication.model.CharacterRepository

class CharacterDetailViewModel : ViewModel() {

    private val _character = MutableLiveData<Character?>()
    val character: LiveData<Character?> = _character

    fun loadCharacter(id: Int) {
        _character.value = CharacterRepository.getCharacterById(id)
    }
}
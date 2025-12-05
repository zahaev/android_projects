package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Character
import com.example.myapplication.model.CharacterRepository

class MainViewModel : ViewModel() {

    private val _characters = MutableLiveData<List<Character>>()
    val characters: LiveData<List<Character>> = _characters

    init {
        loadCharacters()
    }

    private fun loadCharacters() {
        _characters.value = CharacterRepository.characters
    }
}
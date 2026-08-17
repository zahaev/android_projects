package com.example.myapplication.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.di.ServiceLocator

class FavoritesViewModelFactory (
    private val context: Context
):ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       if(modelClass.isAssignableFrom(
           FavoritesViewModel::class.java
       )){
           return FavoritesViewModel(ServiceLocator
               .provideCharacterRepository(context)) as T
       }
        throw IllegalArgumentException(
           "Unknown ViewModel class"
       )
    }
}
package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.West
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel


import com.example.myapplication.viewmodel.FavoritesViewModel
//import com.example.myapplication.viewmodel.FavoritesViewModelFactory

@Composable
fun FavoritesScreen(
    onCharacterClick: (Int) -> Unit,
    onCharacterListClick:() -> Unit
) {
    val context = LocalContext.current
    val viewModel:FavoritesViewModel = hiltViewModel()
    val uiState by viewModel.uiState
        .collectAsStateWithLifecycle()
   //Каждый раз при открытии экрана
   //читаем актуальное состояние Room.

    LaunchedEffect(Unit){
        viewModel.loadFavorites()
    }
    Column(modifier=Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // ============================================================
        // ЗАГОЛОВОК
        // ============================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically

        ) {
            IconButton(onClick = onCharacterListClick) {
                Icon(
                    imageVector = Icons.Default.West,
                    contentDescription = "Назад"
                )
            }
            Text(
                text = "Избранное",
                style = MaterialTheme
                    .typography
                    .headlineSmall
            )
            }

            when {

                uiState.isLoading -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    )
                    {
                        CircularProgressIndicator()
                    }
                }
                // ============================================================
                // ОШИБКА
                // ============================================================
                uiState.errorMessage != null -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = uiState.errorMessage ?: "Ошибка"
                            )
                            IconButton(
                                onClick = {
                                    viewModel.loadFavorites()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Повторить"
                                )
                            }
                        }
                    }
                }

                uiState.characters.isEmpty() -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Text(
                            text = "Нет избранных персонажей",
                            style = MaterialTheme
                                .typography
                                .titleMedium
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.characters,
                            key = { it.id }
                        ) { character ->

                            CharacterCard(
                                character = character,

                                onClick = {
                                    onCharacterClick(
                                        character.id
                                    )
                                },
                                onLongClick = {
                                    viewModel
                                        .toggleFavorite(
                                            character.id
                                        )
                                }
                            )
                        }
                    }
                }
            }

    }
}
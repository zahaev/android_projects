package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.model.domain.model.Character

@Composable
fun CharacterActionDialog(
    character: Character,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(character.name) },
        text = {
            Text(if (character.isFavorite) "Remove from favorites?" else "Add to favorites?")
        },
        confirmButton = {
            TextButton(onClick = onToggleFavorite) {
                Text(if (character.isFavorite) "Remove" else "Add to Favorites")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.model.domain.model.Character

@Composable
fun AddCharacterDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, status: String, species: String, gender: String, imageUrl: String) -> Unit
) {
    var name by remember { mutableStateOf("") }//remember — сохраняем между перерисовками
    var status by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("Human") }
    var gender by remember { mutableStateOf("Unknown") }
    var imageUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Character") },
        text = {
            Column {
                OutlinedTextField(//текстовое поле с рамкой
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))//пустое пространство
                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, status, species, gender, imageUrl)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun CharacterActionDialog(
    character: Character,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
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
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.model.domain.model.Character
import com.example.myapplication.viewmodel.CharacterDetailViewModel
//import com.example.myapplication.viewmodel.CharacterDetailViewModelFactory
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: Int,
    onBack: () -> Unit,//возврат назад
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    val character by viewModel.character.observeAsState()

    LaunchedEffect(characterId) {
        viewModel.loadCharacter(characterId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character?.name ?: "Character") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())//делаем прокручиваемым
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            character?.let { char ->//проверка загружен ли перс
                AsyncImage(

                    model = char.image,
                    contentDescription = char.name,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    text = char.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))//разделение в 8 dp

                Text(
                    text = "Status • ${char.status} ",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Species • ${char.species} ",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Type •  ${char.type}  ",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "First seen in • ${char.origin.name}  ",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Last known location •  ${char.location.name}  ",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Episodes •  ${char.episode}  ",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

            } ?: run {
                CircularProgressIndicator()
            }
        }
    }
}
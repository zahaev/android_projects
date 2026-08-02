package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import coil.request.ImageRequest
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.model.domain.model.Character
import com.example.myapplication.viewmodel.MainViewModel
import com.example.myapplication.viewmodel.MainViewModelFactory
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import android.util.Log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    onCharacterClick: (Int)-> Unit,
    viewModel:MainViewModel=viewModel(
        factory = MainViewModelFactory(LocalContext.current.applicationContext)
    )
)   {
    val characters by viewModel.characters.observeAsState(emptyList())
    val listState = rememberLazyListState()//сохраняет состояние между перерисовками

    //Пагинация
    val shouldLoadMore by remember{
        derivedStateOf{// вычисляемое состояние
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index?:0
            lastVisibleItem >= characters.size - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {//запускает корутину при изменении shouldLoadMore
        if(shouldLoadMore){
            viewModel.loadNextPage()
        }
    }
    LaunchedEffect(Unit) {//выполняется один раз при создании экрана
        viewModel.loadFirstPage()
    }
    var showAddDialog by remember {mutableStateOf/*изеняемое состояние*/(false)}
    var selectedCharacter by remember {mutableStateOf<Character?>(null)}

    Scaffold(
        floatingActionButton ={
            FloatingActionButton(
                onClick = {showAddDialog = true},
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)

            }
        }
    ){padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)//применить отступы от Scaffold
                .background(MaterialTheme.colorScheme.background)

        ){
            Header()
            //список персонажей
            LazyColumn(
                state=listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                items(characters,key= {it.id}){character ->//эл-ы массива characters
                    CharacterCard(//комп-т карточки перс-а
                        character = character,
                        onClick = {onCharacterClick(character.id)},
                        onLongClick={selectedCharacter = character}
                    )
                }
                // Индикатор загрузки
                item{
                    if(characters.isNotEmpty()){
                        Box(
                            modifier=Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment= Alignment.Center
                        ){
                            CircularProgressIndicator(color=MaterialTheme.colorScheme.primary)
                        //индикатор загрузки
                        }

                    }
                }
            }
            Footer()
        }
    }
    // Меню долгого нажатия
    selectedCharacter?.let { character ->
        CharacterActionDialog(
            character = character,
            onDismiss = { selectedCharacter = null },
            onToggleFavorite = {
                viewModel.toggleFavorite(character.id)
                selectedCharacter = null
            },
        )
    }
}
@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Rick and Morty API",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}
@Composable
private fun Footer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B1A1A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Text("Characters: 826", color = Color.White, modifier = Modifier.padding(8.dp))
            Text("Locations: 126", color = Color.White, modifier = Modifier.padding(8.dp))
            Text("Episodes: 51", color = Color.White, modifier = Modifier.padding(8.dp))
        }
        Text("Server status", color = Color.White, modifier = Modifier.padding(top = 8.dp))
    }
}
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun CharacterCard(
    character: Character,
    onClick: () -> Unit,
    onLongClick: () -> Unit
){
    // ← ЛОГИРОВАНИЕ
    // Логирование для отладки
    LaunchedEffect(character.id) {
        android.util.Log.d("CharacterCard", "Loading image for ${character.name}: ${character.image}")

    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(//обработка клика и долгого нажатия
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(character.image.trim().takeIf { it.isNotEmpty() })
                    .crossfade(true)
                    .build(),
                contentDescription = "${character.name} avatar",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray), // Фон пока загружается
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                error = painterResource(id = android.R.drawable.ic_menu_report_image),
                onSuccess = {
                    android.util.Log.d("CharacterCard", "Image loaded successfully for ${character.name}")
                },
                onError = {
                    android.util.Log.e("CharacterCard", "Failed to load image for ${character.name}")
                }
            )
            Column(
                modifier = Modifier
                    .weight(1f)//занять свободное место
                    .padding(start = 12.dp)
            ){
                Text(
                    text = character.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${character.status} • ${character.species}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (character.isFavorite) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Favorite",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                    )
                }

        }
    }
}

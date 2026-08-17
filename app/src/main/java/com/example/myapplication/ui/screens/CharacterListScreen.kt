package com.example.myapplication.ui.screens

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.model.domain.model.Character
import com.example.myapplication.viewmodel.MainViewModel
import com.example.myapplication.viewmodel.MainViewModelFactory


@Composable
fun CharacterListScreen(
    onCharacterClick: (Int) -> Unit,
    onFavoritesClick:() -> Unit,
    viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            LocalContext.current.applicationContext
        )
    )
) {
    // Состояние экрана
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Состояние LazyColumn
    val listState = rememberLazyListState()

    // Персонаж, выбранный долгим нажатием
    var selectedCharacter by remember {
        mutableStateOf<Character?>(null)
    }

    /*
     * ПАГИНАЦИЯ
     *
     * Когда пользователь подходит к концу списка,
     * загружаем следующую страницу.
     */
    LaunchedEffect(
        listState,
        uiState.isLoading,
        uiState.endReached
    ) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo
                .lastOrNull()
                ?.index
        }
            .distinctUntilChanged()
            .collect { lastVisibleItem ->

                val totalItems =
                    listState.layoutInfo.totalItemsCount

                if (
                    lastVisibleItem != null &&
                    lastVisibleItem >= totalItems - 3 &&
                    !uiState.isLoading &&
                    !uiState.endReached
                ) {
                    viewModel.loadNextPage()
                }
            }
    }

    /*
     * ПЕРВАЯ ЗАГРУЗКА
     */
    LaunchedEffect(Unit) {
        if (
            uiState.characters.isEmpty() &&
            !uiState.isLoading
        ) {
            viewModel.loadFirstPage()
        }
    }

    /*
     * При изменении поискового запроса
     * возвращаем список в начало.
     */
    LaunchedEffect(uiState.searchQuery) {
        listState.scrollToItem(0)
    }

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            /*
             * SEARCH BAR
             *
             * Он находится вне LazyColumn,
             * поэтому остаётся видимым во всех состояниях:
             *
             * - загрузка
             * - ошибка
             * - результаты
             * - пустой результат
             */
            Header(
                //передача функций в Header из CharacterListScreen
                searchQuery = uiState.searchQuery,
                onFavoritesClick = onFavoritesClick,
                onSearchQueryChange = viewModel::onSearchQueryChange

            )
            CharacterFilters(

                selectedStatus =
                    uiState.selectedStatus,

                selectedSpecies =
                    uiState.selectedSpecies,

                selectedGender =
                    uiState.selectedGender,

                onStatusChange =
                    viewModel::onStatusChange,

                onSpeciesChange =
                    viewModel::onSpeciesChange,

                onGenderChange =
                    viewModel::onGenderChange,

                onReset =
                    viewModel::resetFilters
            )

            /*
             * ОСНОВНОЕ СОДЕРЖИМОЕ
             */
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {

                when {

                    /*
                     * 1. ПЕРВОНАЧАЛЬНАЯ ЗАГРУЗКА
                     */
                    uiState.isLoading &&
                            uiState.characters.isEmpty() -> {

                        CircularProgressIndicator(
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                    }

                    /*
                     * 2. ОШИБКА
                     */
                    uiState.errorMessage != null &&
                            uiState.characters.isEmpty() -> {

                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = uiState.errorMessage
                                    ?: "Произошла ошибка",
                                color = MaterialTheme
                                    .colorScheme
                                    .error
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Button(
                                onClick = {
                                    viewModel.loadFirstPage()
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Refresh,
                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text("Повторить")
                            }
                        }
                    }

                    /*
                     * 3. ПОИСК ВЫПОЛНЕН,
                     *    НО ПЕРСОНАЖИ НЕ НАЙДЕНЫ
                     */
                    uiState.characters.isEmpty() &&
                            uiState.searchQuery.isNotBlank() -> {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment =
                                Alignment.CenterHorizontally,
                            verticalArrangement =
                                Arrangement.Center
                        ) {

                            Text(
                                text = "Персонажи не найдены",
                                style = MaterialTheme
                                    .typography
                                    .titleMedium
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "По запросу «${uiState.searchQuery}» " +
                                        "ничего не найдено",
                                color = MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                            )
                        }
                    }

                    /*
                     * 4. СПИСОК ПЕРСОНАЖЕЙ
                     */
                    else -> {

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 16.dp
                            ),
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            /*
                             * Персонажи
                             */
                            items(
                                items = uiState.characters,
                                key = { character ->
                                    character.id
                                }
                            ) { character ->

                                CharacterCard(
                                    character = character,

                                    onClick = {
                                        onCharacterClick(
                                            character.id
                                        )
                                    },

                                    onLongClick = {
                                        selectedCharacter = character
                                    }
                                )
                            }

                            /*
                             * Индикатор загрузки
                             * следующей страницы
                             */
                            if (uiState.isLoading) {

                                item {

                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .wrapContentWidth(
                                                Alignment.CenterHorizontally
                                            )
                                    )
                                }
                            }

                            /*
                             * Конец списка
                             */
                            if (
                                uiState.endReached &&
                                uiState.characters.isNotEmpty()
                            ) {

                                item {

                                    Text(
                                        text =
                                            "Достигнут конец списка",

                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .wrapContentWidth(
                                                Alignment.CenterHorizontally
                                            ),

                                        color = MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * ДИАЛОГ ДЕЙСТВИЙ
     *
     * Открывается после долгого нажатия
     * на персонажа.
     */
    selectedCharacter?.let { character ->

        CharacterActionDialog(
            character = character,

            onDismiss = {
                selectedCharacter = null
            },

            onToggleFavorite = {

                viewModel.toggleFavorite(
                    character.id
                )

                selectedCharacter = null
            }
        )
    }
}


/*
 * ============================================================
 * SEARCH HEADER
 * ============================================================
 */

@Composable
private fun Header(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFavoritesClick:() -> Unit
)
{

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 8.dp
            )
    ) {
       Row(
           modifier = Modifier.fillMaxWidth(),
           verticalAlignment = Alignment.CenterVertically
       ) {
           Text(
               text = "Rick and Morty API",
               fontSize = 20.sp,
               fontWeight = FontWeight.Bold,
               color = MaterialTheme
                   .colorScheme
                   .onBackground,

               modifier = Modifier.weight(1f)
               )
           IconButton(
               onClick = onFavoritesClick
           ) {
               Icon(
                   imageVector = Icons.Default.Star,
                   contentDescription = "Избранное"
               )
           }
       }

        OutlinedTextField(
            value = searchQuery,

            onValueChange = {
                onSearchQueryChange(it)
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true,

            placeholder = {
                Text(
                    text = "Поиск персонажа..."
                )
            },

            leadingIcon = {

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Поиск"
                )
            }
        )
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterFilters(
    selectedStatus: String?,
    selectedSpecies: String?,
    selectedGender: String?,
    onStatusChange: (String?) -> Unit,
    onSpeciesChange: (String?) -> Unit,
    onGenderChange: (String?) -> Unit,
    onReset: () -> Unit
) {

    val statuses = listOf(
        "Alive",
        "Dead",
        "unknown"
    )

    val species = listOf(
        "Human",
        "Alien",
        "Humanoid",
        "Animal",
        "Robot",
        "Cronenberg",
        "Disease"
    )

    val genders = listOf(
        "Female",
        "Male",
        "Genderless",
        "unknown"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // Фильтр по статусу
        FilterDropdown(
            title = "Статус",
            selectedValue = selectedStatus,
            values = statuses,
            onValueSelected = onStatusChange
        )

        // Фильтр по species
        FilterDropdown(
            title = "Species",
            selectedValue = selectedSpecies,
            values = species,
            onValueSelected = onSpeciesChange
        )

        // Фильтр по gender
        FilterDropdown(
            title = "Пол",
            selectedValue = selectedGender,
            values = genders,
            onValueSelected = onGenderChange
        )

        // Кнопка сброса
        if (
            selectedStatus != null ||
            selectedSpecies != null ||
            selectedGender != null
        ) {

            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Сбросить фильтры"
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    title: String,
    selectedValue: String?,
    values: List<String>,
    onValueSelected: (String?) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }

    ) {
        OutlinedTextField(

            value = selectedValue?:title,
            onValueChange = {},

            readOnly = true,

            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults
                    .TrailingIcon(
                        expanded = expanded
                    )
            },
            colors = ExposedDropdownMenuDefaults
                .outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded=expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            //Пункт: Все

            DropdownMenuItem(
                text={
                    Text("Все")
                },
                onClick = {

                    onValueSelected(null)

                    expanded = false
                }
            )
            //Значения фильтра
            values.forEach{ value ->

                DropdownMenuItem(
                    text={
                        Text(value)
                    },
                    onClick = {
                        onValueSelected(value)
                        expanded=false
                    }
                )
            }
        }
    }
}

/*
 * ============================================================
 * CHARACTER CARD
 * ============================================================
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterCard(
    character: Character,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),

        shape = RoundedCornerShape(8.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            /*
             * Изображение персонажа
             */
            AsyncImage(

                model = ImageRequest.Builder(
                    LocalContext.current
                )
                    .data(
                        character.image
                            .trim()
                            .takeIf {
                                it.isNotEmpty()
                            }
                    )
                    .crossfade(true)
                    .build(),

                contentDescription =
                    "${character.name} avatar",

                modifier = Modifier
                    .size(60.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(Color.Gray),

                contentScale =
                    ContentScale.Crop,

                placeholder = painterResource(
                    id = android.R.drawable
                        .ic_menu_gallery
                ),

                error = painterResource(
                    id = android.R.drawable
                        .ic_menu_report_image
                )
            )

            /*
             * Информация о персонаже
             */
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {

                Text(
                    text = character.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme
                        .colorScheme
                        .onSurface
                )

                Text(
                    text = "${character.status} • " +
                            character.species,

                    fontSize = 14.sp,

                    color = MaterialTheme
                        .colorScheme
                        .onSurface
                )
            }

            /*
             * Избранное
             */
            if (character.isFavorite) {

                Icon(
                    imageVector = Icons.Default.Star,

                    contentDescription =
                        "Favorite",

                    tint = Color(0xFFFFD700),

                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
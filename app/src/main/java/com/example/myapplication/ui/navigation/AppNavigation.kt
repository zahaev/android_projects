package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.CharacterDetailScreen
import com.example.myapplication.ui.screens.CharacterListScreen
import com.example.myapplication.ui.screens.FavoritesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()//создаём контроллер навигации

    NavHost(//контейнер для экранов навигации
        navController = navController,
        startDestination = "character_list"
    ) {

        // СПИСОК ПЕРСОНАЖЕЙ
        composable("character_list") {
            CharacterListScreen(
                onCharacterClick = { characterId ->
                    navController.navigate(
                        "character_detail/$characterId"
                    ) // переход на  экран character_detail
                },
                onFavoritesClick = {
                    navController.navigate("favorites")
                }
            )
        }


        // ДЕТАЛЬНАЯ ИНФОРМАЦИЯ
        composable(
            route = "character_detail/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?: -1
            CharacterDetailScreen(
                characterId = characterId,
                onBack = { navController.popBackStack() }
            )
        }
        // ИЗБРАННОЕ
        composable("favorites"){
            FavoritesScreen(
                onCharacterClick = {characterId ->
                    navController.navigate("character/$characterId")
                },
                onCharacterListClick={
                    navController.popBackStack()
                }
            )
        }
    }
}
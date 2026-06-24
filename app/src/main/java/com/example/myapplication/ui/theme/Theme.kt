package com.example.myapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF9800),//orange
    secondary = Color(0xFF26262B),//black
    background = Color(0xFF1B1A1A),//dark-gray
    surface = Color(0xFF26262B),//фон карточек
    onPrimary = Color.White,//цвет текста на фонe primary
    onSecondary = Color.White,
    onBackground = Color(0xFFCCCCCC),
    onSurface = Color(0xFFCCCCCC),
)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF9800),
    secondary = Color(0xFF625b71),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content:@Composable ()-> Unit
){
    val colorScheme = if(darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if(!view.isInEditMode){
        SideEffect{// код, который выполняется после каждой перерисовки
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()//конвертация цвета
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content //текущий UI (this.)
    )
}
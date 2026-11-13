package com.example.forca.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = vermelhoClaro,
    onPrimary = Color.White,
    primaryContainer = vermelhoEscuro,
    onPrimaryContainer = textoClaro,
    secondary = cinzaEsverdeado,
    onSecondary = Color.White,
    background = pretoEsverdeado,
    onBackground = textoClaro,
    surface = verdeEscuro,
    onSurface = textoClaro,
    outline = cinzaClaro
)

private val LightColorScheme = lightColorScheme(
    primary = vermelhoEscuro,
    onPrimary = Color.White,
    primaryContainer = vermelhoClaro,
    onPrimaryContainer = Color.White,
    secondary = cinzaEsverdeado,
    onSecondary = Color.White,
    background = fundoClaro,
    onBackground = pretoEsverdeado,
    surface = superficieClara,
    onSurface = pretoEsverdeado,
    outline = cinzaClaro
)

@Composable
fun ForcaTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
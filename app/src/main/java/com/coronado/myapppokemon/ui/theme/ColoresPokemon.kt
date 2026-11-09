package com.coronado.myapppokemon.ui.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** Color oficial de cada tipo de Pokémon (el mismo que usa la franja de tipo en los juegos). */
fun colorDeTipo(tipo: String): Color = when (tipo.trim().lowercase()) {
    "normal" -> Color(0xFFA8A878)
    "fire" -> Color(0xFFF08030)
    "water" -> Color(0xFF6890F0)
    "electric" -> Color(0xFFF8D030)
    "grass" -> Color(0xFF78C850)
    "ice" -> Color(0xFF98D8D8)
    "fighting" -> Color(0xFFC03028)
    "poison" -> Color(0xFFA040A0)
    "ground" -> Color(0xFFE0C068)
    "flying" -> Color(0xFFA890F0)
    "psychic" -> Color(0xFFF85888)
    "bug" -> Color(0xFFA8B820)
    "rock" -> Color(0xFFB8A038)
    "ghost" -> Color(0xFF705898)
    "dragon" -> Color(0xFF7038F8)
    "dark" -> Color(0xFF705848)
    "steel" -> Color(0xFFB8B8D0)
    "fairy" -> Color(0xFFEE99AC)
    else -> Color(0xFF68A090)
}

/** Blanco u oscuro según qué tan claro sea el fondo, para que el texto siempre se lea. */
fun contenidoSobre(fondo: Color): Color =
    if (fondo.luminance() > 0.5f) Color(0xFF1B1B1F) else Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun coloresBarra(fondo: Color = MaterialTheme.colorScheme.primary): TopAppBarColors {
    val c = contenidoSobre(fondo)
    return TopAppBarDefaults.topAppBarColors(
        containerColor = fondo,
        titleContentColor = c,
        navigationIconContentColor = c,
        actionIconContentColor = c
    )
}
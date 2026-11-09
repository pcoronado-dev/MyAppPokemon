package com.coronado.myapppokemon.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val RojoPokebola = Color(0xFFE3350D)
val AzulPokemon = Color(0xFF3B4CCA)
val AmarilloPikachu = Color(0xFFFFDE00)
val FondoPokedex = Color(0xFFF2F6FF)

private val EsquemaPokemon = lightColorScheme(
    primary = RojoPokebola,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD2),
    onPrimaryContainer = Color(0xFF410000),
    secondary = AzulPokemon,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDE1FF),
    onSecondaryContainer = Color(0xFF00105C),
    tertiary = AmarilloPikachu,
    onTertiary = Color(0xFF2B2600),
    background = FondoPokedex,
    onBackground = Color(0xFF1B1B1F),
    surface = FondoPokedex,
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE2E6F5),
    onSurfaceVariant = Color(0xFF45464F)
)

@Composable
fun MyAppPokemonTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaPokemon, content = content)
}
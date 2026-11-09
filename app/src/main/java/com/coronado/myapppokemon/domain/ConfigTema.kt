package com.coronado.myapppokemon.domain

/** Configuración del tema Pokémon. */
object ConfigTema {
    /** Tipo que se muestra la primera vez que se abre la app. */
    const val TIPO_VARIANTE = "fire"

    /** Tipos que se pueden elegir en la parte superior (nombres tal como los usa PokeAPI). */
    val TIPOS = listOf(
        "normal", "fire", "water", "electric", "grass", "ice",
        "fighting", "poison", "ground", "flying", "psychic", "bug",
        "rock", "ghost", "dragon", "dark", "steel", "fairy"
    )
}
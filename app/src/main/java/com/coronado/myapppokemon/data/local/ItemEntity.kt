package com.coronado.myapppokemon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String, // nombre del Pokémon
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false,
    // Tipo de la última lista en la que se descargó (sirve para el color en Favoritos)
    val tipoPrincipal: String = ""
)

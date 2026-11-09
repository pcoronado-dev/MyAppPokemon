package com.coronado.myapppokemon.domain.model

/** Modelo que usa la UI. En este tema cada Item es un Pokémon. El id es el nombre (texto). */
data class Item(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false,
    // Datos que solo trae el segundo endpoint (detalle)
    val tipos: List<String> = emptyList(),
    val alturaMetros: Double? = null,
    val pesoKg: Double? = null,
    val estadisticas: List<Estadistica> = emptyList(),
    val habilidades: List<String> = emptyList()
)

data class Estadistica(val nombre: String, val valor: Int)

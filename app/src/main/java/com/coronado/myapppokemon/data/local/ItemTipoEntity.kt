package com.coronado.myapppokemon.data.local

import androidx.room.Entity

/**
 * Relación Pokémon <-> tipo. Un Pokémon puede estar en varias listas
 * (por ejemplo Charizard está en "fire" y en "flying"), por eso no es una columna de ItemEntity.
 */
@Entity(tableName = "item_tipos", primaryKeys = ["tipo", "itemId"])
data class ItemTipoEntity(
    val tipo: String,
    val itemId: String
)

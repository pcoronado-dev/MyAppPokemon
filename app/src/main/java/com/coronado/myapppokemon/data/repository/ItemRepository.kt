package com.coronado.myapppokemon.data.repository

import com.coronado.myapppokemon.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ItemRepository {
    val tipoActual: StateFlow<String>   // tipo de Pokémon que se está viendo
    val items: Flow<List<Item>>         // sale de Room, solo del tipo actual
    val favoritos: Flow<List<Item>>     // favoritos de todos los tipos
    fun cambiarTipo(tipo: String)
    suspend fun refrescar(): Result<Int>            // Retrofit -> Room; devuelve cuántos guardó
    suspend fun obtenerDetalle(id: String): Result<Item>
    suspend fun alternarFavorito(id: String)
    suspend fun cantidadGuardada(): Int             // total guardado (todos los tipos)
    suspend fun cantidadDelTipoActual(): Int        // guardados del tipo que se está viendo
    suspend fun cantidadFavoritos(): Int
    suspend fun limpiarCache()
}

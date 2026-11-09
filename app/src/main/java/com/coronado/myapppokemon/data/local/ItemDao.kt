package com.coronado.myapppokemon.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query(
        "SELECT i.* FROM items i INNER JOIN item_tipos t ON t.itemId = i.id " +
                "WHERE t.tipo = :tipo ORDER BY i.titulo"
    )
    fun porTipo(tipo: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE esFavorito = 1 ORDER BY titulo")
    fun favoritos(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun buscar(id: String): ItemEntity?

    @Query("SELECT id FROM items WHERE esFavorito = 1")
    suspend fun idsFavoritos(): List<String>

    @Query("SELECT COUNT(*) FROM items")
    suspend fun contar(): Int

    @Query("SELECT COUNT(*) FROM item_tipos WHERE tipo = :tipo")
    suspend fun contarPorTipo(tipo: String): Int

    @Query("SELECT COUNT(*) FROM items WHERE esFavorito = 1")
    suspend fun contarFavoritos(): Int

    @Upsert
    suspend fun guardarTodos(items: List<ItemEntity>)

    @Upsert
    suspend fun guardarRelaciones(relaciones: List<ItemTipoEntity>)

    @Query("UPDATE items SET esFavorito = NOT esFavorito WHERE id = :id")
    suspend fun alternarFavorito(id: String)

    @Query("DELETE FROM items")
    suspend fun borrarTodo()

    @Query("DELETE FROM item_tipos")
    suspend fun borrarRelaciones()
}

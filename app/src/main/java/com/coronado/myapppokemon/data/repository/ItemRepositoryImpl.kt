package com.coronado.myapppokemon.data.repository

import com.coronado.myapppokemon.data.local.ItemDao
import com.coronado.myapppokemon.data.local.ItemTipoEntity
import com.coronado.myapppokemon.data.mapper.aEntity
import com.coronado.myapppokemon.data.mapper.aModelo
import com.coronado.myapppokemon.data.remote.ApiService
import com.coronado.myapppokemon.domain.model.Item
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao,
    tipoInicial: String,
    private val alCambiarTipo: (String) -> Unit = {}
) : ItemRepository {

    private val _tipoActual = MutableStateFlow(tipoInicial)
    override val tipoActual: StateFlow<String> = _tipoActual.asStateFlow()

    // Room es la fuente única de verdad: la UI siempre lee de aquí.
    @OptIn(ExperimentalCoroutinesApi::class)
    override val items: Flow<List<Item>> = _tipoActual
        .flatMapLatest { tipo -> dao.porTipo(tipo) }
        .map { lista -> lista.map { it.aModelo() } }

    override val favoritos: Flow<List<Item>> = dao.favoritos().map { lista -> lista.map { it.aModelo() } }

    override fun cambiarTipo(tipo: String) {
        _tipoActual.value = tipo
        alCambiarTipo(tipo)
    }

    override suspend fun refrescar(): Result<Int> = try {
        val tipo = _tipoActual.value
        val respuesta = api.listar(tipo)
        // Los favoritos se leen justo antes de guardar y se reaplican, para que el Upsert no los pierda.
        val favoritos = dao.idsFavoritos().toSet()
        val nuevos = respuesta.pokemon.map { entrada ->
            entrada.aEntity(tipo = tipo, esFavorito = entrada.pokemon.name in favoritos)
        }
        dao.guardarTodos(nuevos)
        dao.guardarRelaciones(nuevos.map { ItemTipoEntity(tipo = tipo, itemId = it.id) })
        Result.success(nuevos.size)
    } catch (e: IOException) { // sin internet
        Result.failure(e)
    } catch (e: HttpException) { // 404, 500...
        Result.failure(e)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) { // JSON inesperado, etc.
        Result.failure(e)
    }

    override suspend fun obtenerDetalle(id: String): Result<Item> = try {
        val esFavorito = dao.buscar(id)?.esFavorito ?: false
        Result.success(api.detalle(id).aModelo(esFavorito))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // Sin internet: si el Pokémon está guardado, se muestra lo básico que hay en Room.
        val local = dao.buscar(id)
        if (local != null && (e is IOException || e is HttpException)) {
            Result.success(local.aModelo())
        } else {
            Result.failure(e)
        }
    }

    override suspend fun alternarFavorito(id: String) = dao.alternarFavorito(id)
    override suspend fun cantidadGuardada(): Int = dao.contar()
    override suspend fun cantidadDelTipoActual(): Int = dao.contarPorTipo(_tipoActual.value)
    override suspend fun cantidadFavoritos(): Int = dao.contarFavoritos()

    override suspend fun limpiarCache() {
        dao.borrarRelaciones()
        dao.borrarTodo()
    }
}
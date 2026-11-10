package com.coronado.myapppokemon.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coronado.myapppokemon.data.repository.ItemRepository
import com.coronado.myapppokemon.domain.model.Item
import com.coronado.myapppokemon.ui.UiState
import com.coronado.myapppokemon.ui.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleViewModel(
    private val repo: ItemRepository,
    private val id: String
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<Item>>(UiState.Cargando)
    val estado: StateFlow<UiState<Item>> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            repo.obtenerDetalle(id)
                .onSuccess { _estado.value = UiState.Exito(it) }
                .onFailure { _estado.value = UiState.Error(mensajeDeError(it)) }
        }
    }

    fun alternarFavorito() {
        val actual = (_estado.value as? UiState.Exito)?.datos ?: return
        viewModelScope.launch {
            repo.alternarFavorito(actual.id)
            _estado.value = UiState.Exito(actual.copy(esFavorito = !actual.esFavorito))
        }
    }
}


package com.coronado.myapppokemon.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coronado.myapppokemon.data.repository.ItemRepository
import com.coronado.myapppokemon.notification.Notificador
import com.coronado.myapppokemon.ui.UiState
import com.coronado.myapppokemon.ui.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminResumen(val totalGuardado: Int, val totalFavoritos: Int)

class AdminViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<AdminResumen>>(UiState.Cargando)
    val estado: StateFlow<UiState<AdminResumen>> = _estado.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _ocupado = MutableStateFlow(false)
    val ocupado: StateFlow<Boolean> = _ocupado.asStateFlow()

    init {
        actualizarTotales()
    }

    private suspend fun leerResumen() = AdminResumen(repo.cantidadGuardada(), repo.cantidadFavoritos())

    fun actualizarTotales() {
        viewModelScope.launch { _estado.value = UiState.Exito(leerResumen()) }
    }

    fun sincronizar() {
        viewModelScope.launch {
            _ocupado.value = true
            repo.refrescar()
                .onSuccess { n ->
                    _mensaje.value = "Sincronizado: se guardaron $n elementos"
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }
                .onFailure { _mensaje.value = mensajeDeError(it) }
            _estado.value = UiState.Exito(leerResumen())
            _ocupado.value = false
        }
    }

    fun limpiarCache() {
        viewModelScope.launch {
            _ocupado.value = true
            repo.limpiarCache()
            _mensaje.value = "Caché limpiada"
            _estado.value = UiState.Exito(leerResumen())
            _ocupado.value = false
        }
    }
}

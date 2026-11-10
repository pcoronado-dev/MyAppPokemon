package com.coronado.myapppokemon.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coronado.myapppokemon.data.repository.ItemRepository
import com.coronado.myapppokemon.domain.model.Item
import com.coronado.myapppokemon.notification.Notificador
import com.coronado.myapppokemon.ui.UiState
import com.coronado.myapppokemon.ui.mensajeDeError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListaViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    /** Tipo de Pokémon que se está viendo (fire, water, grass...). */
    val tipo: StateFlow<String> = repo.tipoActual

    private var refrescoTerminado = false
    private var trabajoRefresco: Job? = null

    init {
        viewModelScope.launch {
            // La lista sale SIEMPRE de Room. Se mantiene "Cargando" hasta que haya datos
            // o termine el primer refresco (así no se ve "vacío" antes de tiempo).
            repo.items.collect { lista ->
                if (lista.isNotEmpty() || refrescoTerminado) {
                    _estado.value = UiState.Exito(lista)
                } else {
                    _estado.value = UiState.Cargando
                }
            }
        }
        refrescar()
    }

    fun seleccionarTipo(nuevo: String) {
        if (nuevo == repo.tipoActual.value) return
        trabajoRefresco?.cancel()
        refrescoTerminado = false
        _aviso.value = null
        repo.cambiarTipo(nuevo)
        _estado.value = UiState.Cargando
        refrescar()
    }

    fun refrescar() {
        trabajoRefresco?.cancel()
        trabajoRefresco = viewModelScope.launch {
            val resultado = repo.refrescar()
            refrescoTerminado = true
            resultado
                .onSuccess { n ->
                    if (_estado.value is UiState.Cargando || _estado.value is UiState.Error) {
                        _estado.value = UiState.Exito(emptyList())
                    }
                    _aviso.value = null
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }
                .onFailure { e ->
                    if (repo.cantidadDelTipoActual() == 0) {
                        _estado.value = UiState.Error(mensajeDeError(e))
                    } else {
                        _aviso.value = "Sin conexión: mostrando datos guardados"
                    }
                }
        }
    }

    fun alternarFavorito(id: String) {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }
}

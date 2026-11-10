package com.coronado.myapppokemon.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coronado.myapppokemon.data.auth.AuthRepository
import com.coronado.myapppokemon.domain.model.Sesion
import com.coronado.myapppokemon.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginFormState(
    val cargando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null // aviso positivo, por ejemplo "Cuenta creada"
)

/** Maneja la sesión de toda la app: ingreso, registro y cierre de sesión. */
class LoginViewModel(private val auth: AuthRepository) : ViewModel() {

    // Cargando = revisando si ya hay sesión guardada; Exito(null) = no hay sesión
    private val _sesion = MutableStateFlow<UiState<Sesion?>>(UiState.Cargando)
    val sesion: StateFlow<UiState<Sesion?>> = _sesion.asStateFlow()

    private val _formulario = MutableStateFlow(LoginFormState())
    val formulario: StateFlow<LoginFormState> = _formulario.asStateFlow()

    // Se vuelve true cuando la cuenta se creó bien: la pantalla de registro regresa al login
    private val _registroCompletado = MutableStateFlow(false)
    val registroCompletado: StateFlow<Boolean> = _registroCompletado.asStateFlow()

    init {
        viewModelScope.launch {
            _sesion.value = UiState.Exito(auth.sesionActual())
        }
    }

    fun iniciarSesion(correo: String, clave: String) {
        // Validación: si hay campos vacíos NO se llama a Firebase
        if (correo.isBlank() || clave.isBlank()) {
            _formulario.value = LoginFormState(error = "Escribe tu correo y tu contraseña")
            return
        }
        viewModelScope.launch {
            _formulario.value = LoginFormState(cargando = true)
            auth.iniciarSesion(correo.trim(), clave)
                .onSuccess {
                    _formulario.value = LoginFormState()
                    _sesion.value = UiState.Exito(it)
                }
                .onFailure {
                    _formulario.value = LoginFormState(error = it.message ?: "No se pudo iniciar sesión")
                }
        }
    }

    fun registrar(nombre: String, correo: String, clave: String, confirmar: String) {
        val error = when {
            nombre.isBlank() -> "Escribe tu nombre"
            correo.isBlank() -> "Escribe tu correo"
            clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            clave != confirmar -> "Las contraseñas no coinciden"
            else -> null
        }
        if (error != null) {
            _formulario.value = LoginFormState(error = error)
            return
        }
        viewModelScope.launch {
            _formulario.value = LoginFormState(cargando = true)
            auth.registrar(nombre.trim(), correo.trim(), clave)
                .onSuccess {
                    _formulario.value = LoginFormState(mensaje = "Cuenta creada. Ahora inicia sesión")
                    _registroCompletado.value = true
                }
                .onFailure {
                    _formulario.value = LoginFormState(error = it.message ?: "No se pudo crear la cuenta")
                }
        }
    }

    /** Se llama al abrir el registro para no arrastrar errores viejos. */
    fun limpiarError() {
        _formulario.value = LoginFormState()
    }

    fun registroAtendido() {
        _registroCompletado.value = false
    }

    fun cerrarSesion() {
        auth.cerrarSesion()
        _formulario.value = LoginFormState()
        _sesion.value = UiState.Exito(null)
    }
}

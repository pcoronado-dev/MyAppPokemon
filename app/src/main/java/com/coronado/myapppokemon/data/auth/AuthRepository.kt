package com.coronado.myapppokemon.data.auth

import com.coronado.myapppokemon.domain.model.Sesion

/** Error de autenticación con un mensaje listo para mostrar. */
class AuthException(mensaje: String) : Exception(mensaje)

interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    /**
     * Crea la cuenta en Firebase Auth y su perfil en Firestore (siempre con rol "usuario").
     * Al terminar NO deja la sesión abierta: la persona debe iniciar sesión.
     */
    suspend fun registrar(nombre: String, correo: String, clave: String): Result<Unit>
    suspend fun sesionActual(): Sesion? // null si no hay sesión
    fun cerrarSesion()
}

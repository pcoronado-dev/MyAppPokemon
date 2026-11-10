package com.coronado.myapppokemon.data.auth

import android.util.Log
import com.coronado.myapppokemon.domain.model.Rol
import com.coronado.myapppokemon.domain.model.Sesion
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> = try {
        val uid = auth.signInWithEmailAndPassword(correo, clave).await().user?.uid
            ?: throw AuthException("No se pudo iniciar sesión")
        Result.success(leerSesion(uid, correo))
    } catch (e: CancellationException) {
        throw e
    } catch (e: AuthException) {
        Result.failure(e)
    } catch (e: FirebaseAuthInvalidUserException) {
        Result.failure(AuthException("Correo o contraseña incorrectos"))
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.failure(AuthException("Correo o contraseña incorrectos"))
    } catch (e: FirebaseNetworkException) {
        Result.failure(AuthException("Sin conexión a internet"))
    } catch (e: Exception) {
        Result.failure(AuthException("No se pudo iniciar sesión. Intenta de nuevo"))
    }

    override suspend fun registrar(nombre: String, correo: String, clave: String): Result<Unit> = try {
        val uid = auth.createUserWithEmailAndPassword(correo, clave).await().user?.uid
            ?: throw AuthException("No se pudo crear la cuenta")

        // El rol NUNCA lo elige la persona: toda cuenta nueva es "usuario".
        // Los administradores se asignan a mano en la consola de Firebase.
        try {
            // withTimeoutOrNull evita quedarse cargando si Firestore no responde
            withTimeoutOrNull(10_000) {
                db.collection("usuarios").document(uid)
                    .set(mapOf("rol" to "usuario", "nombre" to nombre))
                    .await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("Registro", "No se pudo guardar el perfil en Firestore", e)
        }

        // Firebase deja la sesión abierta al crear la cuenta: se cierra para que
        // la persona vuelva al login e ingrese con sus credenciales.
        auth.signOut()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: AuthException) {
        Result.failure(e)
    } catch (e: FirebaseAuthUserCollisionException) {
        Result.failure(AuthException("Ese correo ya está registrado"))
    } catch (e: FirebaseAuthWeakPasswordException) {
        Result.failure(AuthException("La contraseña debe tener al menos 6 caracteres"))
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.failure(AuthException("El correo no es válido"))
    } catch (e: FirebaseNetworkException) {
        Result.failure(AuthException("Sin conexión a internet"))
    } catch (e: Exception) {
        Result.failure(AuthException("No se pudo crear la cuenta. Intenta de nuevo"))
    }

    override suspend fun sesionActual(): Sesion? {
        val usuario = auth.currentUser ?: return null
        val correo = usuario.email.orEmpty()
        return try {
            leerSesion(usuario.uid, correo)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // No se pudo leer el rol (sin red y sin caché): se entra con el rol mínimo
            Sesion(usuario.uid, correo, Rol.USUARIO)
        }
    }

    override fun cerrarSesion() = auth.signOut()

    // El rol NO se decide en la app: se lee de Firestore (usuarios/{uid}.rol).
    // Si Firestore no responde a tiempo o falla, se entra con el rol mínimo (usuario)
    // en vez de dejar la pantalla cargando para siempre.
    private suspend fun leerSesion(uid: String, correo: String): Sesion {
        val rol = try {
            val doc = withTimeoutOrNull(8_000) {
                db.collection("usuarios").document(uid).get().await()
            }
            if (doc?.getString("rol") == "admin") Rol.ADMIN else Rol.USUARIO
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("Auth", "No se pudo leer el rol", e)
            Rol.USUARIO
        }
        return Sesion(uid, correo, rol)
    }
}

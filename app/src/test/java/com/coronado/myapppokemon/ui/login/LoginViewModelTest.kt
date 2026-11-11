package com.coronado.myapppokemon.ui.login

import com.coronado.myapppokemon.data.auth.AuthRepository
import com.coronado.myapppokemon.domain.model.Rol
import com.coronado.myapppokemon.domain.model.Sesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FakeAuthRepositoryForTest : AuthRepository {
    var sesionActualRetorno: Sesion? = null
    var llamadasIniciarSesion = 0
    var llamadasRegistrar = 0
    var resultadoIniciarSesion: Result<Sesion> = Result.success(Sesion("1", "test@test.com", Rol.USUARIO))
    var resultadoRegistrar: Result<Unit> = Result.success(Unit)

    override suspend fun sesionActual(): Sesion? = sesionActualRetorno

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> {
        llamadasIniciarSesion++
        return resultadoIniciarSesion
    }

    override suspend fun registrar(nombre: String, correo: String, clave: String): Result<Unit> {
        llamadasRegistrar++
        return resultadoRegistrar
    }

    override fun cerrarSesion() {
        sesionActualRetorno = null
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun iniciar_sesion_con_campos_vacios_muestra_error_y_no_llama_a_firebase() = runTest(testDispatcher) {
        val auth = FakeAuthRepositoryForTest()
        val viewModel = LoginViewModel(auth)
        testScheduler.advanceUntilIdle()

        viewModel.iniciarSesion("", "")
        testScheduler.advanceUntilIdle()

        assertEquals("Escribe tu correo y tu contraseña", viewModel.formulario.value.error)
        assertEquals(0, auth.llamadasIniciarSesion)
    }

    @Test
    fun registrar_con_contrasena_menor_a_seis_caracteres_muestra_error() = runTest(testDispatcher) {
        val auth = FakeAuthRepositoryForTest()
        val viewModel = LoginViewModel(auth)
        testScheduler.advanceUntilIdle()

        viewModel.registrar("Juan", "juan@test.com", "12345", "12345")
        testScheduler.advanceUntilIdle()

        assertEquals("La contraseña debe tener al menos 6 caracteres", viewModel.formulario.value.error)
        assertEquals(0, auth.llamadasRegistrar)
    }

    @Test
    fun registrar_con_contrasenas_desiguales_muestra_error() = runTest(testDispatcher) {
        val auth = FakeAuthRepositoryForTest()
        val viewModel = LoginViewModel(auth)
        testScheduler.advanceUntilIdle()

        viewModel.registrar("Juan", "juan@test.com", "123456", "654321")
        testScheduler.advanceUntilIdle()

        assertEquals("Las contraseñas no coinciden", viewModel.formulario.value.error)
        assertEquals(0, auth.llamadasRegistrar)
    }

    @Test
    fun validacion_fallida_no_llama_a_firebase() = runTest(testDispatcher) {
        val auth = FakeAuthRepositoryForTest()
        val viewModel = LoginViewModel(auth)
        testScheduler.advanceUntilIdle()

        viewModel.registrar("", "juan@test.com", "123456", "123456")
        testScheduler.advanceUntilIdle()

        assertEquals("Escribe tu nombre", viewModel.formulario.value.error)
        assertEquals(0, auth.llamadasRegistrar)
        assertEquals(0, auth.llamadasIniciarSesion)
    }
}

package com.coronado.myapppokemon.ui.lista

import com.coronado.myapppokemon.data.repository.ItemRepository
import com.coronado.myapppokemon.domain.model.Item
import com.coronado.myapppokemon.notification.Notificador
import com.coronado.myapppokemon.ui.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class FakeItemRepositoryForTest(
    initialTipo: String = "fire",
    initialItems: List<Item> = emptyList(),
    private val refreshResult: Result<Int> = Result.success(2),
    private val cantidadLocal: Int = 0
) : ItemRepository {
    private val _tipoActual = MutableStateFlow(initialTipo)
    override val tipoActual: StateFlow<String> = _tipoActual.asStateFlow()

    private val _items = MutableStateFlow(initialItems)
    override val items: Flow<List<Item>> = _items.asStateFlow()

    private val _favoritos = MutableStateFlow<List<Item>>(emptyList())
    override val favoritos: Flow<List<Item>> = _favoritos.asStateFlow()

    var refrescosContador = 0
    var tipoCambiadoVeces = 0

    override fun cambiarTipo(tipo: String) {
        if (tipo != _tipoActual.value) {
            tipoCambiadoVeces++
        }
        _tipoActual.value = tipo
    }

    override suspend fun refrescar(): Result<Int> {
        refrescosContador++
        return refreshResult
    }

    override suspend fun obtenerDetalle(id: String): Result<Item> {
        val item = _items.value.find { it.id == id } ?: Item(id, "Test", "", null, false)
        return Result.success(item)
    }

    override suspend fun alternarFavorito(id: String) {
        val actual = _items.value.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it }
        _items.value = actual
    }

    override suspend fun cantidadGuardada(): Int = _items.value.size
    override suspend fun cantidadDelTipoActual(): Int = if (_items.value.isNotEmpty()) _items.value.size else cantidadLocal
    override suspend fun cantidadFavoritos(): Int = _items.value.count { it.esFavorito }
    override suspend fun limpiarCache() {
        _items.value = emptyList()
    }
}

class FakeNotificadorForTest : Notificador {
    var ultimoTitulo: String? = null
    var ultimoTexto: String? = null
    var vecesNotificado = 0

    override fun mostrar(titulo: String, texto: String) {
        ultimoTitulo = titulo
        ultimoTexto = texto
        vecesNotificado++
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {

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
    fun carga_exitosa_actualiza_estado_a_exito_y_muestra_notificacion() = runTest(testDispatcher) {
        val repo = FakeItemRepositoryForTest(
            initialItems = listOf(Item("1", "Charmander", "url", null, false)),
            refreshResult = Result.success(1)
        )
        val notificador = FakeNotificadorForTest()

        val viewModel = ListaViewModel(repo, notificador)
        testScheduler.advanceUntilIdle()

        val estado = viewModel.estado.value
        assertTrue(estado is UiState.Exito)
        assertEquals(1, (estado as UiState.Exito).datos.size)
        assertEquals("Charmander", estado.datos[0].titulo)
        assertEquals(1, notificador.vecesNotificado)
        assertEquals("Datos actualizados", notificador.ultimoTitulo)
    }

    @Test
    fun error_sin_datos_guardados_muestra_estado_error() = runTest(testDispatcher) {
        val repo = FakeItemRepositoryForTest(
            initialItems = emptyList(),
            refreshResult = Result.failure(IOException("Sin conexión")),
            cantidadLocal = 0
        )
        val notificador = FakeNotificadorForTest()

        val viewModel = ListaViewModel(repo, notificador)
        testScheduler.advanceUntilIdle()

        val estado = viewModel.estado.value
        assertTrue(estado is UiState.Error)
    }

    @Test
    fun error_con_datos_guardados_muestra_aviso_sin_conexion() = runTest(testDispatcher) {
        val itemsGuardados = listOf(Item("1", "Charmander", "url", null, false))
        val repo = FakeItemRepositoryForTest(
            initialItems = itemsGuardados,
            refreshResult = Result.failure(IOException("Sin conexión")),
            cantidadLocal = 1
        )
        val notificador = FakeNotificadorForTest()

        val viewModel = ListaViewModel(repo, notificador)
        testScheduler.advanceUntilIdle()

        val estado = viewModel.estado.value
        assertTrue(estado is UiState.Exito)
        assertEquals("Sin conexión: mostrando datos guardados", viewModel.aviso.value)
    }

    @Test
    fun lista_vacia_emite_exito_con_lista_vacia() = runTest(testDispatcher) {
        val repo = FakeItemRepositoryForTest(
            initialItems = emptyList(),
            refreshResult = Result.success(0)
        )
        val notificador = FakeNotificadorForTest()

        val viewModel = ListaViewModel(repo, notificador)
        testScheduler.advanceUntilIdle()

        val estado = viewModel.estado.value
        assertTrue(estado is UiState.Exito)
        assertTrue((estado as UiState.Exito).datos.isEmpty())
    }

    @Test
    fun marcar_favorito_llama_al_repositorio() = runTest(testDispatcher) {
        val item = Item("1", "Charmander", "url", null, false)
        val repo = FakeItemRepositoryForTest(initialItems = listOf(item))
        val notificador = FakeNotificadorForTest()

        val viewModel = ListaViewModel(repo, notificador)
        testScheduler.advanceUntilIdle()

        viewModel.alternarFavorito("1")
        testScheduler.advanceUntilIdle()

        val estado = viewModel.estado.value as UiState.Exito
        assertTrue(estado.datos[0].esFavorito)
    }

    @Test
    fun cambio_de_tipo_cambia_el_tipo_actual_y_refresca() = runTest(testDispatcher) {
        val repo = FakeItemRepositoryForTest(initialTipo = "fire")
        val notificador = FakeNotificadorForTest()

        val viewModel = ListaViewModel(repo, notificador)
        testScheduler.advanceUntilIdle()

        viewModel.seleccionarTipo("water")
        testScheduler.advanceUntilIdle()

        assertEquals("water", viewModel.tipo.value)
        assertEquals(1, repo.tipoCambiadoVeces)
    }

    @Test
    fun que_no_se_vuelva_a_descargar_al_elegir_el_mismo_tipo() = runTest(testDispatcher) {
        val repo = FakeItemRepositoryForTest(initialTipo = "fire")
        val notificador = FakeNotificadorForTest()

        val viewModel = ListaViewModel(repo, notificador)
        testScheduler.advanceUntilIdle()

        val refrescosIniciales = repo.refrescosContador
        viewModel.seleccionarTipo("fire")
        testScheduler.advanceUntilIdle()

        assertEquals("fire", viewModel.tipo.value)
        assertEquals(0, repo.tipoCambiadoVeces)
        assertEquals(refrescosIniciales, repo.refrescosContador)
    }
}

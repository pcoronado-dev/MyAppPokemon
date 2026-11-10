package com.coronado.myapppokemon.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.coronado.myapppokemon.AppContainer
import com.coronado.myapppokemon.domain.model.Rol
import com.coronado.myapppokemon.ui.AppViewModelFactory
import com.coronado.myapppokemon.ui.DetalleViewModelFactory
import com.coronado.myapppokemon.ui.UiState
import com.coronado.myapppokemon.ui.admin.AdminScreen
import com.coronado.myapppokemon.ui.admin.AdminViewModel
import com.coronado.myapppokemon.ui.components.Cargando
import com.coronado.myapppokemon.ui.detalle.DetalleScreen
import com.coronado.myapppokemon.ui.detalle.DetalleViewModel
import com.coronado.myapppokemon.ui.favoritos.FavoritosScreen
import com.coronado.myapppokemon.ui.favoritos.FavoritosViewModel
import com.coronado.myapppokemon.ui.lista.ListaScreen
import com.coronado.myapppokemon.ui.lista.ListaViewModel
import com.coronado.myapppokemon.ui.login.LoginScreen
import com.coronado.myapppokemon.ui.login.LoginViewModel
import com.coronado.myapppokemon.ui.login.RegistroScreen

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val LISTA = "lista"
    const val FAVORITOS = "favoritos"
    const val ADMIN = "admin"
    const val DETALLE = "detalle/{id}"
    fun detalle(id: String) = "detalle/$id"
}

@Composable
fun AppNav(container: AppContainer) {
    val factory = remember { AppViewModelFactory(container) }

    // LoginViewModel vive a nivel de Activity: guarda la sesión y el rol de toda la app
    val loginVm: LoginViewModel = viewModel(factory = factory)
    val sesionState by loginVm.sesion.collectAsStateWithLifecycle()

    // La pantalla inicial se decide UNA vez, cuando se sabe si hay sesión guardada (R11)
    var inicio by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(sesionState) {
        val s = sesionState
        if (inicio == null && s is UiState.Exito) {
            inicio = if (s.datos == null) Rutas.LOGIN else Rutas.LISTA
        }
    }
    val destinoInicial = inicio
    if (destinoInicial == null) {
        Cargando()
        return
    }

    // El rol viene de Firestore (lo lee el repositorio); aquí solo se usa para decidir qué se ve
    val sesion = when (val s = sesionState) {
        is UiState.Exito -> s.datos
        else -> null
    }
    val esAdmin = sesion?.rol == Rol.ADMIN

    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = destinoInicial) {

        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = loginVm,
                onIngreso = {
                    nav.navigate(Rutas.LISTA) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                },
                onIrARegistro = { nav.navigate(Rutas.REGISTRO) }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                viewModel = loginVm,
                onRegistrado = { nav.popBackStack() }, // vuelve al login
                onVolver = { nav.popBackStack() }
            )
        }

        composable(Rutas.LISTA) {
            val vm: ListaViewModel = viewModel(factory = factory)
            ListaScreen(
                viewModel = vm,
                esAdmin = esAdmin,
                onItemClick = { id -> nav.navigate(Rutas.detalle(id)) },
                onFavoritos = { nav.navigate(Rutas.FAVORITOS) },
                onAdmin = { nav.navigate(Rutas.ADMIN) },
                onCerrarSesion = {
                    loginVm.cerrarSesion()
                    nav.navigate(Rutas.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } }
                }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            val vm: DetalleViewModel = viewModel(key = "detalle-$id", factory = DetalleViewModelFactory(container, id))
            DetalleScreen(viewModel = vm, onVolver = { nav.popBackStack() })
        }

        composable(Rutas.FAVORITOS) {
            val vm: FavoritosViewModel = viewModel(factory = factory)
            FavoritosScreen(
                viewModel = vm,
                onItemClick = { id -> nav.navigate(Rutas.detalle(id)) },
                onVolver = { nav.popBackStack() }
            )
        }

        composable(Rutas.ADMIN) {
            if (esAdmin) {
                val vm: AdminViewModel = viewModel(factory = factory)
                AdminScreen(viewModel = vm, onVolver = { nav.popBackStack() })
            } else {
                // Un usuario normal nunca debería llegar aquí
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes acceso a este panel")
                }
            }
        }
    }
}


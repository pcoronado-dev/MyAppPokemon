package com.coronado.myapppokemon.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coronado.myapppokemon.AppContainer
import com.coronado.myapppokemon.ui.admin.AdminViewModel
import com.coronado.myapppokemon.ui.detalle.DetalleViewModel
import com.coronado.myapppokemon.ui.favoritos.FavoritosViewModel
import com.coronado.myapppokemon.ui.lista.ListaViewModel
import com.coronado.myapppokemon.ui.login.LoginViewModel

/** Crea los ViewModel con las dependencias del AppContainer. */
class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(LoginViewModel::class.java) ->
            LoginViewModel(container.authRepository) as T
        modelClass.isAssignableFrom(ListaViewModel::class.java) ->
            ListaViewModel(container.itemRepository, container.notificador) as T
        modelClass.isAssignableFrom(FavoritosViewModel::class.java) ->
            FavoritosViewModel(container.itemRepository) as T
        modelClass.isAssignableFrom(AdminViewModel::class.java) ->
            AdminViewModel(container.itemRepository, container.notificador) as T
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}

/** El detalle necesita además el id del Pokémon que se va a mostrar. */
class DetalleViewModelFactory(
    private val container: AppContainer,
    private val id: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        DetalleViewModel(container.itemRepository, id) as T
}
